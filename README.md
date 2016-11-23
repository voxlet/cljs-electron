[![Build Status](https://travis-ci.org/Gonzih/cljs-electron.svg?branch=master)](https://travis-ci.org/Gonzih/cljs-electron)

# Clojurified Electron

*This fork replaces Reagent with Om.next, among other changes*

![](https://raw.githubusercontent.com/Gonzih/cljs-electron/master/demo.gif)

My attempt to recreate ClojureScript development workflow while developing desktop apps with [electron](http://electron.atom.io/).

## What is currently included

* ClojureScript (init script and ui code)
* Figwheel for interactive development
* Om.next for UI

## Running it

```shell
npm install                      # install electron binaries

foreman start                    # compile cljs and start figwheel
- OR -
lein cljsbuild auto main-dev     # ...and start Figwheel in Cursive

npm start                        # start electron from another terminal
```

## Releasing

```shell
lein cljsbuild once ui-release   # compile ui code
lein cljsbuild once main-release # compile electron initialization code

npm start                        # start electron to test that everything works
```

After that you can follow [distribution guide for the electron.](https://github.com/atom/electron/blob/master/docs/tutorial/application-distribution.md)
