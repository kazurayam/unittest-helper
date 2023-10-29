<https://github.com/kazurayam/selenium-webdriver-java/issues/8>

# MarkdownUtils

## ATX heading examples

A study how the HTML id values of ATX headings are generated by GitHub Flavored Markdown processor

### The simplest case

Here is the simplest case

### issue3 Asciidoc & Markdown

Ampersand character (&) matters.

### issue8 Under bar \_ character

Under bar character (\_) matters.

### Parentheses ( and ) characters

Parentheses ( and ) matters

### issue12 Dot . character

Dot character (.) matters.

### issue13 Hyphen - character

Hyphen character (-) matters.

### issue14 Slash / character

Slash character (/) matters.

### issue15 Colon : character

Colon character (:) matters.

## "publishdocs" --- a custom Gradle task

    $ cd MarkdownUtils
    $ gradle publishdocs

This single line is equivalent to the following operation in the command line:

    ---
    $ cd MarkdownUtils
    $ cd docs
    $ ./indexconv.sh -t
    $ cd ..
    $ git add .
    $ git commit -m "update docs"
    $ git push
    ---

## indexconv.sh

TO BE AUTHORED

## MarkdownUtil

### Problem to solve

The `index_.adoc` is originally authored in AsciiDoc.
I will execute the following command.

    $ cd docs
    $ ./indexconv.sh -t

The `readmeconv.sh` will generate `README.md` file. It will have
the following "Table of Contents" section.

    -   [MarkdownUtils](#_markdownutils)
        -   [com.kazurayam.markdownutils.PandocMarkdownTranslator](#_com_kazurayam_markdownutils_pandocmarkdowntranslator)
            -   [Problem to solve](#_problem_to_solve)

    # MarkdownUtils
    ...

When I push this README.md up to GitHub, the link from TOC
to the body sections does not work. The links are broken.

What do I mean "the links are broken"? If you open <https://github.com/kazurayam/MarkdownUtils/> using
any web browser and view the HTML source, you will find a section:

            <a href="#_problem_to_solve">Problem to solve</a>

and also you will find a sction:

I will execute the follwoing command:

    $ cd $MarkdownUtils
    $ java -jar ./libs/MarkdownUtils-0.1.0-SNAPSHOT ./README.md ./temp.md

The jar contains the `com.kazurayam.markdownutils.PandocMarkdownTranslator` class.
The above command will call `translateFile("./README.md", "temp.md")` emthod, and
generate a new file `temp.md`, which will have the following section:

    -   [MarkdownUtils](#markdownutils)
        -   [com.kazurayam.markdownutils.PandocMarkdownTranslator](#com-kazurayam-markdownutils-pandocmarkdowntranslator)
            -   [Problem to solve](#problem-to-solve)

    # MarkdownUtils
    ...

Please find the link symbols are slightly amended.
The amended link symbols conforms to GitHub Flavoured Mardown spec.
When `temp.md` is pushed to GitHub, the links in TOC to text body will work.

The `PandocMarkdownTranslator` does this small patching over Markdown texts
generated by `pandoc`.