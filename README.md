# c-migrator

Migrates and rollbacks cassandra migrations with the use of ragtime and ragtime-alia

## Installation

Install lein, run with REPL.

## Usage

Create %configname%.properties file in properties dir.
It should contain:
host = cassandra_host
port=port
migrations_dir = path_to_migrations
keyspace = keyspace
 

From project directory run:
lein migrate %configname% -> migrates all files from %migrations_dir%
lein rollback %configname% -> rollbacks last migration
lein rollback %configname% N -> rollbacks last N migrations
lein rollback %configname% %id% -> rollbacks until find the needed %id% of migration, then stops


    $ java -jar c-migrator-0.1.0-standalone.jar [args]

## Options


## Examples

### Bugs

When running:
lein rollback %configname% %id% 
it should drop an exception if %id% is non-existant. 
However, some possible ragtime bug makes rollbacks go till all rollbacks will be applied, making DB clear.
Bug is reported: https://github.com/weavejester/ragtime/issues/109
In order to avoid the bug, checking of ID is programmed manually.

### Any Other Sections
### That You Think
### Might be Useful

## License

Copyright © 2017 Unicenter

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
