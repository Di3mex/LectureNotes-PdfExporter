# LectureNotes PdfExporter

A simple Java program that is able to export Notebooks created by [LectureNotes](https://play.google.com/store/apps/details?id=com.acadoid.lecturenotestrial) to pdf format.

## Compilation
We use maven to handle our dependencies and build process

```
mvn clean package
```

## Usage

For the moment this is a command line only app, so you need to know how to open your command line.

```
java -jar PDF-Exporter-0.1-SNAPSHOT.jar -f /path/to/lecturenotes/folder
```
