# Branch name template
#
# Available placeholders:
#   {{TYPE}}                  — one of: feature, fix, hotfix, chore, refactor, docs, test
#   {{TICKET_SUMMARY_SLUG}}   — Jira summary slugified: lowercase, words joined by hyphens,
#                               non-alphanumeric characters stripped (e.g. add-user-profile-screen)
#
# Result examples:
#   feature/add-user-profile-screen
#   fix/login-crash
#   chore/bump-target-sdk-37

{{TYPE}}/{{TICKET_SUMMARY_SLUG}}
