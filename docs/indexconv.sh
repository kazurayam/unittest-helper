#!/bin/bash

# Convert all the files under the current directory
# with name ending with `*_.adoc` into `*.md`.
# `*_.adoc` is an Asciidoc document file,
# `*.md` is a Markdown document file.
# E.g, `index_.adoc` will be converted into `index_.adoc`
# Except ones with `_` as prefix.
#
# How to run this: in the command line, just type
# `> ./indexconv.sh`
#
# Can generate Table of Content in the output *.md file by specifying `-t` option
# `> ./indexconv.sh -t`
#

requireTOC=false

optstring="t"
while getopts ${optstring} arg; do
  case ${arg} in
    t)
        requireTOC=true
        ;;
    ?)
        ;;
  esac
done

function processFile() {
  fname=$1
  #echo "fname=${fname}"
  #  using Asciidoctor, convert a *.adoc file into a docbook in XML
  md=${fname//adoc/md}
  xml=${fname//adoc/xml}
  echo "converting $fname into $md"
  asciidoctor -b docbook -a leveloffset=+1 -o - "$fname" > "$xml"
  if [ $requireTOC = true ]; then
    # using Pandoc, generate a Markdown file with TOC
    cat "$xml" | pandoc --standalone --toc --toc-depth 5 --markdown-headings=atx --wrap=preserve -t markdown_strict -f docbook - > "$md"
  else
    # using Pandoc, generate a Markdown file without TOC
    cat "$xml" | pandoc --markdown-headings=atx --wrap=preserve -t markdown_strict -f docbook - > "$md"
  fi
  #echo deleting $xml
  rm -f "$xml"

  # We named `index_.adoc` rather than `index.adoc` because GitHub puts precedence to `index.adoc` over `index.md`. We want `index.md` to be presented first, not `*.adoc`. Therefore we named our adoc file with `*_.adoc` postfix.
  # This trick required further treatment.
  # `index_.adoc` will result `index_.adoc`. But we
  # want the final result to be `index.md`.
  # So, we will rename `*_.md` into `*.md`.
  # in other words, chomp an underline character (_) before `.md``
  # e.g,
  #   ./index_.adoc    -> ./index.md
  #   ./index-ja_.md -> ./index-ja.md
  newmd=${md%_.md}.md
  echo renaming $md to $newmd
  mv $md $newmd

  # slightly modify the TOC in the generated *.md file
  #    - [Solution 1](#_solution_1)
  # will be translated into
  #    - [Solution 1](#solution-1)
  cat $newmd > temp.md
  java -jar MarkdownUtils-0.2.0.jar temp.md $newmd
  rm temp.md
  echo amended TOC in $newmd

  # just a blank line to sperate the *.adoc files processed
  echo ""
}



# iterate over all *.adoc files
find . -iname "*_.adoc" -type f -maxdepth 1 -not -name "_*.adoc" | while read fname; do
  processFile $fname
done
