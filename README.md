# korekto-java-basics-grader
Korekto grader for Java basics exercise

[![Build](https://github.com/lernejo/korekto-java-basics-grader/actions/workflows/build.yml/badge.svg)](https://github.com/lernejo/korekto-java-basics-grader/actions)
[![codecov](https://codecov.io/gh/lernejo/korekto-java-basics-grader/branch/main/graph/badge.svg?token=EUNNO5IANB)](https://codecov.io/gh/lernejo/korekto-java-basics-grader)
![License](https://img.shields.io/badge/License-Elastic_License_v2-blue)

Exercise subject: [here](EXERCISE_fr.adoc)

# How to launch
You will need these 2 env vars:
* `GH_LOGIN` your GitHub login
* `GH_TOKEN` a [**P**ersonal **A**ccess **T**oken](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens#creating-a-personal-access-token-classic) with permissions:
    * (classic) : `repo` and `user`
    * (fine-grained):
        * Repository permissions:
            * **Actions**: `Read-only`
            * **Contents**: `Read-only`

```bash
git clone git@github.com:lernejo/korekto-java-basics-grader.git
cd korekto-java-basics-grader
./mvnw compile exec:java -Dexec.args="-s=$GH_LOGIN" -Dgithub_token="$GH_TOKEN"
```

### With intelliJ

![Demo Run Configuration](https://raw.githubusercontent.com/lernejo/korekto-toolkit/main/docs/demo_run_configuration.png)
