# CLAUDE.md — retorch-st-fullteaching

Demonstrator of the [RETORCH](https://github.com/giis-uniovi/retorch) framework: a Java end-to-end suite (Selenium, including WebRTC
video sessions) for [FullTeaching](https://github.com/elastest/full-teaching) (Spring Boot + Angular + OpenVidu), whose sources are in `sut/`.
See `README.md` for the user documentation; the upstream FullTeaching README is kept at the end of it.

## RETORCH rules (do not break)

- Every test method carries `@AccessMode(...)` annotations (`giis.retorch.annotations`) that declare the logical resources it uses and
  how (`READONLY`/`READWRITE`, concurrency, sharing). The TJob schedule of the `Jenkinsfile` is **generated from them**.
  - Never drop or alter existing `@AccessMode` annotations when refactoring.
  - Never rename test classes or methods: the `Jenkinsfile` references them as `Class#method`.
  - A new test method needs sensible `@AccessMode` values (copy a sibling) and is only run by CI after regenerating the pipeline.
- `Jenkinsfile`, `.retorch/scripts/`, `.retorch/envfiles/` (except `local.env`), `.retorch/executionplan.json` and the resource model in
  `.retorch/configurations/*SystemResources.json` are **generated** by `orchestrate.ps1` (it needs the
  `retorch-orchestration-*-standalone.jar` of the RETORCH tool in the repository root). Do not edit them by hand; put SUT-specific
  commands in `.retorch/customscriptscode/` and settings in `.retorch/configurations/retorchCI.properties`.
- Only `retorch-annotations` is a dependency of the tests; the orchestration code is never part of this project.
- `profiling=true` in `retorchCI.properties` makes the pipeline measure the resources of every TJob and publish
  `artifacts/usageprofile/usageprofile.html`. `.retorch/customscriptscode/custom-usageprofile` temporarily builds the profiling tool from
  source; delete it when a release publishes the jar.

## Layout

| Path | What it is |
|---|---|
| `src/test/java/com/fullteaching/e2e/no_elastest/` | The suite: `functional/test` (`UserTest`, `LoggedForumTest`, `LoggedLinksTests`, `UnLoggedLinksTests`, `student/`, `teacher/`, `media/`), `common/` (browser users, navigation utilities), `utils/` |
| `src/test/resources/inputs/test.properties` | Test inputs |
| `sut/` | FullTeaching sources and its `Dockerfile` (the image also builds the Angular front-end) |
| `docker-compose.yml`, `docker-compose.local.yml` | SUT stack for CI / local override that publishes the ports (https://localhost:5000) |
| `local.env` | Variables of the local deployment (`TJOB_NAME=local`, media server and database images) |
| `deploy.sh` / `deploy.ps1` | Local deployment (`--no-build`/`-NoBuild`, `--down`/`-Down`) |
| `.retorch/` | Generated pipeline files + `customscriptscode/`, `configurations/retorchCI.properties` |
| `orchestrate.ps1` + `Jenkinsfile` | Regenerate / the generated pipeline: 10 TJobs (`tjoba`…`tjobj`) in 3 stages |

RETORCH resources: `webbrowser`, `webserver`, `executor`, `loginservice`, `course`, `session`, `forum`, `files`, `attenders`, `information`,
`configuration`, `openvidu`, `openvidumock`.

## Build and test

- Java 11 (`maven.compiler.source/target`), JUnit Jupiter, Selenium 4, Selema.
- Local run: `./deploy.sh` (or `.\deploy.ps1`), then `mvn test` (or `-Dtest=<Class>`). Target resolution: `SUT_URL` property → `SUT_URL`
  env var → `app.url` property (default `https://localhost:5000`, self-signed certificate). The TJob name is read from `tjob_name`.
- `TEACHER_BROWSER` / `STUDENT_BROWSER` (environment) choose the browsers; `SELENOID_PRESENT` runs them in a Selenoid / Selenium Grid hub.
- The tests of `functional/test/media` use the OpenVidu / Kurento media server containers of the SUT stack.

## CI

- Jenkins, agent label `xretorch-agent`. Every TJob deploys its **own** copy of the SUT (`--env-file .retorch/envfiles/<tjob>.env
  -p <tjob>`) on the external network `jenkins_network`, runs its tests and tears it down; the TJobs of a stage run in parallel.
- The TJob runs `mvn test -Dtest="<Class#method>,..." -DTJOB_NAME=<tjob> -DSUT_URL=<url>`.
- `pom.xml` builds into `target/${TJOB_NAME}` so parallel TJobs never share compiled classes or reports (profile `local-execution` sets
  `TJOB_NAME=local` when the property is absent).
- `.github/workflows/build.yml` builds the project and runs the SonarCloud analysis; the E2E tests run in Jenkins.
- `coi-setup` builds the `full-teaching` image once before any TJob; each TJob only pulls the images that are not built locally
  (`custom-tjob-setup`). The application is reached at `https://full-teaching-<tjob>:5000` inside the Docker network.

## Keeping this file useful

Record current state and non-obvious gotchas only (history is in `git log`). If the code and this file disagree, tell the user.
