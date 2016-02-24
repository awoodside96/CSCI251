rm $1/version_control.txt
git log --pretty=format:"%h - %s" > $1/version_control.txt
