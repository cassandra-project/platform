#!/bin/sh
if [ -f "VERSION" ]
then
echo "File VERSION exists, removing ..."
rm VERSION
else
echo "File VERSION not existing"
fi
echo "Creating new VERSION file ..."
echo $1 > VERSION

