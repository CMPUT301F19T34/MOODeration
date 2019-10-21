#!/bin/bash

# https://benlimmer.com/2013/12/26/automatically-publish-javadoc-to-gh-pages-with-travis-ci/

if [ "$TRAVIS_REPO_SLUG" == "CMPUT301F19T34/MOODeration" ] && [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$TRAVIS_BRANCH" == "master" ]; then

  echo -e "Publishing javadoc...\n"

  cp -R build/docs/javadoc $HOME/javadoc-latest

  cd $HOME
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "travis-ci"
  git clone --quiet --branch=gh-pages https://${GITHUB_TOKEN}@github.com/CMPUT301F19T34/MOODeration gh-pages >/dev/null 2>&1

  cd gh-pages
  git rm -rf .
  cp -Rf $HOME/javadoc-latest/* ./
  git add -f .
  git commit -m "Latest javadoc on successful travis build $TRAVIS_BUILD_NUMBER auto-pushed to gh-pages"
  git push -fq origin gh-pages >/dev/null 2>&1

  echo -e "Published Javadoc to gh-pages.\n"
  
fi

