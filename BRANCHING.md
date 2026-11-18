# Branching and Git Workflow

## 1. Branch Naming Conventions

All development work must be carried out on branches separate from main.

Use the following naming conventions:

- feature/<description> - New application features.
- fix/<description> - Bug fixes.
- docs/<description> - Documentation changes.
- chore/<description> - Maintenance and configuration changes.
- hotfix/<description> - Urgent production fixes.

Use lowercase words separated by hyphens.

Examples:
- feature/user-authentication
- fix/booking-validation
- docs/update-project-guide
- chore/update-dependencies

## 2. Pull Request Workflow

1. Start from an updated main branch.
2. Create a branch using the naming conventions above.
3. Implement the changes and commit them.
4. Push the branch to the remote repository.
5. Open a Pull Request targeting main.
6. Provide a clear description of the changes.
7. Request a review and address any feedback.
8. Obtain the required approval and ensure required checks pass.
9. Merge the Pull Request into main.
10. Delete the feature branch after merging.

Direct pushes to main are not permitted.

## 3. Commit Message Conventions

Use the Conventional Commits format:

<type>: <short description>

Common types:

- feat: A new feature.
- fix: A bug fix.
- docs: Documentation changes.
- chore: Maintenance tasks.
- test: Adding or updating tests.
- refactor: Code restructuring without changing behaviour.
- build: Build system or dependency changes.

Examples:
- feat: add user registration
- fix: correct booking validation
- docs: add branching guidelines
- chore: add Maven gitignore rules

Use imperative, concise descriptions.

## 4. Release Tagging Conventions

The project follows Semantic Versioning:

MAJOR.MINOR.PATCH

- MAJOR: Incompatible changes.
- MINOR: Backward-compatible functionality.
- PATCH: Backward-compatible bug fixes.

Release tags must use the v prefix.

Examples:
- v1.0.0
- v1.1.0
- v1.0.1

Create annotated tags on the appropriate merged release commit:

git tag -a v1.0.0 -m "Release v1.0.0"

Push release tags to GitHub:

git push origin v1.0.0

Only stable, reviewed changes merged into main should be tagged for release.
