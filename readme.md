# In The Shell

## prepare

* java21
* javafx 21.0.1

## build

just 

```shell
mvn clean package
```

## run jar

For example, we prepare javafx libraries in path `~/jobs/javafx-sdk-21.0.1` 。

Then, run it

```shell
java --module-path ~/jobs/javafx-sdk-21.0.1/lib \
      --add-modules javafx.controls,javafx.fxml \
      -jar target/in-the-shell-1.0-SNAPSHOT-jar-with-dependencies.jar
```
