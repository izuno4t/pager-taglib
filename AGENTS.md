# Repository Guidelines

## Project Structure & Module Organization

- `src/main/java` contains the library implementation (packages under
  `com.jsptags.navigation.pager`).
- `src/main/resources` is for runtime resources (currently minimal).
- `src/test/java` contains unit tests (JUnit 5).
- `config/` holds static analysis configuration for Checkstyle, PMD, and
  SpotBugs.
- `docs/` includes project documentation and reports.

## Build, Test, and Development Commands

- `./mvnw clean test` cleans the workspace and runs the full test compile and
  test suite.
- `./mvnw test` runs tests without deleting `target/`.
- `./mvnw verify` runs tests plus quality checks (JaCoCo report, Checkstyle,
  PMD, SpotBugs).
- `./mvnw -DskipTests package` builds the JAR without running tests.

## Coding Style & Naming Conventions

- Language: Java 11, UTF-8 source encoding (see `pom.xml`).
- Indentation: 4 spaces; avoid tabs.
- Naming: packages are lower-case; classes are `PascalCase`; constants use
  `UPPER_SNAKE_CASE`.
- Static analysis: Checkstyle, PMD, and SpotBugs run in `verify` phase using
  configs under `config/`.

## Testing Guidelines

- Frameworks: JUnit Jupiter (`org.junit.jupiter`), AssertJ, Mockito.
- Test classes follow `*Test` naming (e.g., `PagesTagTest`).
- Run all tests with `./mvnw test`; include `./mvnw verify` before submitting.

## Commit & Pull Request Guidelines

- Commit messages follow an imperative style seen in history (e.g., "Add ...",
  "Update ...", "Refactor ...").
- Pull requests should include a concise summary, test results (command and
  outcome), and linked issues when applicable.
- If changes affect output or docs, include brief notes on updated behavior or
  documentation files.

## Security & Configuration Tips

- Do not change Maven coordinates, distribution targets, or build plugins
  without explicit approval.
- Keep configuration files under `config/` in sync with any rule changes and
  document rationale in the PR.
