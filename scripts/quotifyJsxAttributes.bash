#!/bin/bash

help()
{
	echo "Usage:";
	echo;
	echo "	bash $(basename $0) [-i] <filename.tsx>";
	echo "	bash $(basename $0) --search-replace";
	echo "	bash $(basename $0) --find";
	echo "	bash $(basename $0) --help";
	echo;
	echo "Filename must be .jsx or .tsx";
	echo;
	echo "Will replace .jsx/.tsx attributes starting with double quotes to brackets with single quotes";
	echo '	e.g. className="my-class" -> className={'"'"'my-class'"'"'}';
	echo
	echo "Passing --search-replace will find all .jsx/.tsx files in the current directory recursively and quotify";
}

findFiles()
{
	 find -name "*.tsx" \
		 | xargs awk '
			  /[a-zA-Z]+="[^"]+"/ {double[FILENAME]++} 
			  /[a-zA-Z]+=\x27[^\x27]+\x27/ {single[FILENAME]++} 
			  END {
			  	for (file in double) {
					single[file]++;
					single[file]--;
					print file, double[file], single[file];
					delete single[file];
				}
				for (file in single) {
					double[file]++;
					double[file]--;
					print file, double[file], single[file];
				}
			      }' \
		 | column -t --table-columns 'FILE,DOUBLE COUNT,SINGLE COUNT';
}

quotify()
{
	inplaceOption=();

	if [[ -z "$1" ]]; then
		help;
		exit 1;
	fi

	if [[ "$1" == "-i" ]]; then
		inplaceOption=( -i inplace );
		shift;
	fi

	file="$1";
	fileExt="${file: -4}";

	if [[ "$fileExt" != ".tsx" && "$fileExt" != ".jsx" ]]; then
		echo "Invalid file extension: [$fileExt] for file: $file";
		echo;
		help;
		exit 2;
	fi

	awk "${inplaceOption[@]}" -v quote="'" -v doubleRegex='([a-zA-Z]+)="([^"]+)"' -v singleRegex="([a-zA-Z]+)='([^']+)'" '
	$0 ~ doubleRegex { 
		$0 = gensub(doubleRegex, "\\1={" quote "\\2" quote "}", "g"); 
		doubleCount++; 
	}
	$0 ~ singleRegex { 
		$0 = gensub(singleRegex, "\\1={" quote "\\2" quote "}", "g"); 
		singleCount++; 
	}
	{ print }
	END { print "Replaced quotes in [" FILENAME "]: replace count = [double:", doubleCount ", single:", singleCount "]" > "/dev/stderr"; }
	' "$file";
}


case "${1:-}" in
	"--help"|"")
		help;
		;;

	"--search-replace")
		export -f quotify;
		findFiles | tail +2 | awk '{print $1}' | xargs bash -c 'for arg; do quotify -i "$arg"; done' _;
		;;

	"--find")
		findFiles;
		;;

	*)
		quotify "$@";
		;;
esac

