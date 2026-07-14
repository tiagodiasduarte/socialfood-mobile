# Branch name template
#
# Available placeholders:
#   {{BRANCH_PREFIX}}         — "feature" for stories/tasks, "bugfix" for bugs
#   {{TICKET_ID_LOWER}}       — Jira ticket ID in lowercase (e.g. apps-7)
#   {{TICKET_SUMMARY_SLUG}}   — Jira summary slugified: lowercase, words joined by hyphens,
#                               non-alphanumeric characters stripped (e.g. fix-author-card-spacing)
#
# Result examples:
#   feature/apps-7-add-user-profile-screen
#   bugfix/apps-12-fix-login-crash

{{BRANCH_PREFIX}}/{{TICKET_ID_LOWER}}-{{TICKET_SUMMARY_SLUG}}
