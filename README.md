# LectureNotes PdfExporter

A simple Java program that is able to export Notebooks created by [LectureNotes](https://play.google.com/store/apps/details?id=com.acadoid.lecturenotestrial) to pdf format.

## Compilation
We use maven to handle our dependencies and build process

```
mvn clean package
```

## Downloads
You can grab precompiled builds at [my jenkins server](http://ci.extrahardmode.com/job/LN_PdfExporter/)

## Usage

For the moment this is a command line only app, so you need to know how to open your command line.
The following command will export a notebook into a pdf in the same directory.

```
java -jar PDF-Exporter-0.1-SNAPSHOT.jar -f /path/to/lecturenotes/folder
```

If you want to change the output file add the -o switch
```
java -jar PDF-Exporter-0.1-SNAPSHOT.jar -f /path/to/lecturenotes/folder -o /mydocs/exported.pdf
```
