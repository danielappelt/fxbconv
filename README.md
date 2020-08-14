# fxbconv

fxbconv consists of two [Babashka](https://github.com/borkdude/babashka) / Clojure scripts for converting a [FXB bulk preset](https://en.wikipedia.org/wiki/Virtual_Studio_Technology#Presets) file into individual FXP, or [Carla](https://github.com/falkTX/Carla) preset files.

## Warning

This is still alpha software!

## Background

FXB and FXP plugin preset formats are not very well supported in the open source world. This program follows a very simple plan. Without checking much of the internal structure it just splits up FXB bank preset files at the format's magic chunk sequence.

## Installation

1.) Install Babashka according to the provided [instructions](https://github.com/borkdude/babashka#installation).

2.) Download all .clj files from this repository, or clone this repository.

## Usage

```bash
bb fxb2fxp.clj foobar.fxb
```

This will create individual FXP preset files named foobar.fxb_&lt;n>.fxp with n being a running index over the included presets.

```bash
bb fxb2carxs.clj foobar.fxb
```

This will create individual Carla preset files named foobar.fxb_&lt;n>.carxs with n being a running index over the included presets.


## Related projects

* [ardour2fxp](https://github.com/spotlightkid/ardour2fxp)
* [fxbjs](https://github.com/zetadrums/fxbjs)

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## License

fxb2fxp is [MIT licensed](LICENSE).
