#!/usr/bin/env bash
# Jira helper functions — source this file, do not run it directly.
# Requires: JIRA_BASE_URL, JIRA_EMAIL, JIRA_API_TOKEN

_jira_check_env() {
  if [[ -z "${JIRA_BASE_URL:-}" ]]; then
    echo "Error: JIRA_BASE_URL is not set." >&2
    return 1
  fi
  if [[ -z "${JIRA_EMAIL:-}" ]]; then
    echo "Error: JIRA_EMAIL is not set. Use your Atlassian account email." >&2
    return 1
  fi
  if [[ -z "${JIRA_API_TOKEN:-}" ]]; then
    echo "Error: JIRA_API_TOKEN is not set. Generate one at https://id.atlassian.com/manage-profile/security/api-tokens" >&2
    return 1
  fi
}

# Wraps curl with HTTP response validation. Outputs the response body on success.
# Usage: _jira_curl [curl-args...]  (pass -X, -H, -d, URL etc. as normal)
_jira_curl() {
  local tmp
  tmp=$(mktemp)
  local http_code
  http_code=$(curl -s -o "$tmp" -w "%{http_code}" "$@")

  if [[ "$http_code" == "401" ]]; then
    echo "Error: Authentication failed. Check JIRA_EMAIL and JIRA_API_TOKEN." >&2
    return 1
  elif [[ "$http_code" == "404" ]]; then
    echo "Error: Resource not found (HTTP 404)." >&2
    return 1
  elif [[ "$http_code" != "200" && "$http_code" != "201" && "$http_code" != "204" ]]; then
    echo "Error: Jira returned HTTP ${http_code}." >&2
    return 1
  fi

  cat "$tmp"
}

jira_next_to_refine() {
  _jira_check_env || return 1
  jira_search 'project=APPS AND status="To Refine" AND issuetype != Epic ORDER BY rank ASC'
}

jira_next_to_do() {
  _jira_check_env || return 1
  jira_search 'project=APPS AND status="To Do" AND issuetype != Epic ORDER BY rank ASC'
}

jira_search() {
  _jira_check_env || return 1
  local jql="$1"
  local payload
  payload=$(jq -n --arg jql "$jql" '{"jql":$jql,"maxResults":10,"fields":["key","summary","issuetype"]}')
  local response
  response=$(_jira_curl -X POST \
    -u "${JIRA_EMAIL}:${JIRA_API_TOKEN}" \
    -H "Content-Type: application/json" \
    -d "$payload" \
    "${JIRA_BASE_URL}/rest/api/3/search/jql") || return 1

  local count
  count=$(printf '%s\n' "$response" | jq '.issues | length')
  echo "  Found $count ticket(s):" >&2
  printf '%s\n' "$response" | jq -r '.issues[] | "  - \(.key) [\(.fields.issuetype.name)] \(.fields.summary)"' >&2

  printf '%s\n' "$response" | jq -r '.issues[0].key // empty'
}

_jira_get_field() {
  _jira_check_env || return 1
  local ticket="$1" field="$2" filter="$3"
  _jira_curl \
    -u "${JIRA_EMAIL}:${JIRA_API_TOKEN}" \
    "${JIRA_BASE_URL}/rest/api/3/issue/${ticket}?fields=${field}" \
    | jq -r "$filter"
}

jira_get_summary() {
  _jira_get_field "$1" summary '.fields.summary'
}

jira_get_type() {
  _jira_get_field "$1" issuetype '.fields.issuetype.name // "Story"'
}

jira_transition() {
  _jira_check_env || return 1
  local ticket="$1"
  local status="$2"

  local transitions_response
  transitions_response=$(_jira_curl \
    -u "${JIRA_EMAIL}:${JIRA_API_TOKEN}" \
    "${JIRA_BASE_URL}/rest/api/3/issue/${ticket}/transitions") || return 1

  local available
  available=$(printf '%s\n' "$transitions_response" | jq -r '[.transitions[].name] | join(", ")')
  echo "  Available transitions for $ticket: $available"

  local transition_id
  transition_id=$(printf '%s\n' "$transitions_response" | jq -r --arg s "$status" '.transitions[] | select(.name == $s) | .id')

  if [ -z "$transition_id" ]; then
    echo "  Warning: transition '$status' not found — skipping."
    return
  fi

  local payload
  payload=$(jq -n --arg id "$transition_id" '{"transition":{"id":$id}}')

  _jira_curl -X POST \
    -u "${JIRA_EMAIL}:${JIRA_API_TOKEN}" \
    -H "Content-Type: application/json" \
    -d "$payload" \
    "${JIRA_BASE_URL}/rest/api/3/issue/${ticket}/transitions" > /dev/null || return 1

  echo "  → $ticket moved to '$status'"
}

jira_comment() {
  _jira_check_env || return 1
  local ticket="$1"
  local text="$2"

  _jira_curl -X POST \
    -u "${JIRA_EMAIL}:${JIRA_API_TOKEN}" \
    -H "Content-Type: application/json" \
    -d "$(jq -n --arg body "$text" '{body:{type:"doc",version:1,content:[{type:"paragraph",content:[{type:"text",text:$body}]}]}}')" \
    "${JIRA_BASE_URL}/rest/api/3/issue/${ticket}/comment" > /dev/null || return 1

  echo "  → Comment posted on $ticket"
}

jira_update_description() {
  _jira_check_env || return 1
  local ticket="$1"
  local script_path
  if [ -n "${BASH_SOURCE:-}" ]; then
    script_path="${BASH_SOURCE[0]}"
  elif [ -n "${ZSH_VERSION:-}" ]; then
    script_path="$(eval 'print -r -- ${(%):-%x}')"
  else
    script_path="$0"
  fi
  local plans_dir
  plans_dir="$(cd "$(dirname "$script_path")" && pwd)/../.plans"

  local content
  if [[ -f "${plans_dir}/${ticket}-jira.md" ]]; then
    content=$(cat "${plans_dir}/${ticket}-jira.md")
  elif [[ -f "${plans_dir}/${ticket}.md" ]]; then
    content=$(cat "${plans_dir}/${ticket}.md")
  else
    echo "Error: No plan file found for ${ticket} in ${plans_dir}" >&2
    echo "Run the planner agent first to generate the plan." >&2
    return 1
  fi

  local payload
  payload=$(jq -n --arg content "$content" '{
    fields: {
      description: {
        type: "doc",
        version: 1,
        content: [{
          type: "codeBlock",
          attrs: { language: "markdown" },
          content: [{ type: "text", text: $content }]
        }]
      }
    }
  }')

  _jira_curl -X PUT \
    -u "${JIRA_EMAIL}:${JIRA_API_TOKEN}" \
    -H "Accept: application/json" \
    -H "Content-Type: application/json" \
    -d "$payload" \
    "${JIRA_BASE_URL}/rest/api/3/issue/${ticket}" > /dev/null || return 1

  echo "  → ${ticket} description updated"
  echo "  View at: ${JIRA_BASE_URL}/browse/${ticket}"
}

jira_get_issue() {
  _jira_check_env || return 1
  local ticket="$1"
  local response
  response=$(_jira_curl \
    -u "${JIRA_EMAIL}:${JIRA_API_TOKEN}" \
    -H "Accept: application/json" \
    "${JIRA_BASE_URL}/rest/api/3/issue/${ticket}") || return 1

  printf '%s\n' "$response" | jq -r '
    "Key:         " + .key,
    "Summary:     " + .fields.summary,
    "Status:      " + .fields.status.name,
    "Priority:    " + (.fields.priority.name // "None"),
    "Assignee:    " + (.fields.assignee.displayName // "Unassigned"),
    "Reporter:    " + (.fields.reporter.displayName // "Unknown"),
    "Type:        " + .fields.issuetype.name,
    "Created:     " + .fields.created[:10],
    "Updated:     " + .fields.updated[:10],
    "",
    "Description:",
    (.fields.description.content[]?.content[]?.text? // "" | select(. != ""))
  '
}
