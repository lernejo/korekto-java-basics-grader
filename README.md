# korekto-java-basics-grader
Korekto grader for Java basics exercise

[![Build](https://github.com/lernejo/korekto-java-basics-grader/actions/workflows/build.yml/badge.svg)](https://github.com/lernejo/korekto-java-basics-grader/actions)
[![codecov](https://codecov.io/gh/lernejo/korekto-java-basics-grader/branch/main/graph/badge.svg?token=EUNNO5IANB)](https://codecov.io/gh/lernejo/korekto-java-basics-grader)

### With Maven

```bash
mvn compile exec:java -Dexec.args="-s=mySlug"
```

### With intelliJ

To launch the tool locally, run `com.github.lernejo.korekto.toolkit.launcher.GradingJobLauncher` with the
argument `-s=mySlug`

![Demo Run Configuration](https://raw.githubusercontent.com/lernejo/korekto-toolkit/main/docs/demo_run_configuration.png)

## GitHub API rate limiting

When using the grader a lot, GitHub may block API calls for a certain amount of time (criterias change regularly).
This is because by default GitHub API are accessed anonymously.

To increase the API usage quota, use a [Personal Access Token](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token) in order to access GitHub APIs authenticated.

Such a token can be supplied to the grader tool through the system property : `-Dgithub_token=<your token>`

Like so:

```bash
mvn compile exec:java -Dexec.args="-s=yourGitHubLogin" -Dgithub_token=<your token>
