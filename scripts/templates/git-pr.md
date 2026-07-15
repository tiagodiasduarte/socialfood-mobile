# PR description template
#
# Available placeholders:
#   {{TICKET_ID}}      — Jira ticket ID (e.g. APPS-7)
#   {{SUMMARY}}        — Jira ticket summary/title
#   {{JIRA_BASE_URL}}  — Jira base URL (e.g. https://tiagodiasduarte.atlassian.net)
#   {{CHANGES}}        — bullet list of key changes made (one "- " line per change)
#   {{SCREENSHOTS}}    — screenshots when the diff touches UI code, else "N/A"

## Summary

{{SUMMARY}}

Jira: [{{TICKET_ID}}]({{JIRA_BASE_URL}}/browse/{{TICKET_ID}})

## Changes

{{CHANGES}}

## Test plan

- [ ] Unit tests pass (`./gradlew :composeApp:allTests`)
- [ ] No regressions in existing functionality
- [ ] Acceptance criteria from Jira ticket met

## Screenshots (if UI change)

{{SCREENSHOTS}}

🤖 Generated with [Claude Code](https://claude.com/claude-code)
