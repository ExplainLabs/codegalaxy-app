package io.codegalaxy.domain

import scommons.websql.Database
import scommons.websql.io.SqliteContext

class CodeGalaxyDBContext(db: Database) extends SqliteContext(db)
