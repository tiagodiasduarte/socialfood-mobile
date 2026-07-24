# Git Conventions

Source of truth for commit messages, branch names, PR titles, and PR descriptions across this repo. 
Render these conventions for the automated pipeline (`.claude/agents/pipeline.md`). Follow the
same conventions for manual branches/PRs too.


## Branch naming

Format:

`<type>/<short-description>`

**Types:**

| Type       | Use for                              |
|------------|--------------------------------------|
| `feature`  | New functionality                    |
| `fix`      | Bug fixes                            |
| `hotfix`   | Urgent production fixes              |
| `chore`    | Tooling, dependencies, CI, config    |
| `refactor` | Code changes with no behavior change |
| `docs`     | Documentation only                   |
| `test`     | Test-only changes                    |

**Examples:**
- `feature/guide-photo-upload`
- `fix/session-token-refresh`
- `chore/bump-target-sdk-37`

Use lowercase, hyphen-separated words. Keep the description short and specific.

## Pull request titles

Format:

`<type>(<scope>): <short summary>`

Map branch `<type>` to the Conventional Commits type: `feature` → `feat`,
`hotfix` → `fix`, everything else unchanged (`fix`, `chore`, `refactor`, `docs`, `test`).

**Examples:**
- `feat(guides): add photo upload to guide editor`
- `fix(auth): refresh token on 401 instead of forcing re-login`
- `chore(ci): add tests workflow`

## Pull request description template

Use exactly the sections defined below unless explicitly requested.
```markdown
## Summary
<!-- What does this PR do and why? -->

## Changes
-
```

## Commit message

Format:

`<type>(<scope>): <short summary>`

**Examples:**
- `feat(guides): add photo upload to guide editor`
- `fix(auth): refresh token on 401 instead of forcing re-login`
- `chore(ci): add tests workflow`

## Base branch

Branch off `develop` and open pull requests against `develop`, not `main`. `main` only receives merges from `develop` for releases.

## Branch protection

`main` and `develop` require:
- Pull requests before merging (no direct pushes)
- Passing CI status checks
- No force pushes or deletions
