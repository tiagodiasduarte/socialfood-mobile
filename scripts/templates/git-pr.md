# PR description template
#
# Available placeholders:
#   {{TICKET_ID}}      — Jira ticket ID (e.g. APPS-7)
#   {{SUMMARY}}        — Jira ticket summary/title
#   {{JIRA_BASE_URL}}  — Jira base URL (e.g. https://tiagodiasduarte.atlassian.net)

## Summary

- Implements {{TICKET_ID}}: {{SUMMARY}}

## Jira

[{{TICKET_ID}}]({{JIRA_BASE_URL}}/browse/{{TICKET_ID}})

## Test plan

- [ ] Unit tests pass (`./gradlew :composeApp:allTests`)
- [ ] No regressions in existing functionality
- [ ] Acceptance criteria from Jira ticket met

🤖 Generated with [Claude Code](https://claude.com/claude-code)
