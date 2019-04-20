To Do
1. ~print out files in current dir, for now just one per line~
2. ~use default sort, alphabetical, dirs first~
3. ~print with correct colours~ mostly done, except for obscure file types
4. change default print to columns (figure out how to determine columns)
5. implement arg parsing, only flags
6. implement each option
7. check for path arg
8. implement unix wild cards in path/ignore options

# Requirements
Mainly, it should behave like a user would _expect_ the real one to.
-Paths can be mixed with flags in any order. You can do path -flag or -flag path with the same effect.
-You can specify flags multiple times, but later flags will overwrite earlier ones.
-Paths, however, are cumulative.
-If you’re listing multiple dirs, it’ll label them.
-There are some further idiosyncrasies, but not enough to notice.
    -If you list specific files, they’re never labelled by dir, and if you do it in more than once place, they get mixed together.
    -If you list specific files in the current dir they don’t have paths, but if you specify the path at all, they will be fully-pathed.


## Supported Flags
-a, --all                  do not ignore entries starting with .
-A, --almost-all           do not list implied . and ..
-h, --human-readable       with -l and -s, print sizes like 1K 234M 2G etc.
    --si                   likewise, but use powers of 1000 not 1024
-I, --ignore=PATTERN       do not list implied entries matching shell PATTERN
-l                         use a long listing format
-m                         fill width with a comma separated list of entries
-1                         list one file per line.

## Colours
Read and use colours from $LS_COLORS.

The env string is separated by colons. It is a mixture of general rules, which are represented by two-letter codes, and file-extension specific colours. The syntax looks like general Unix wildcards, but it’s not possible to use arbitrary matches, only extensions. Thus you can do *.md, but not README.* — it will complain that the var is invalid.
