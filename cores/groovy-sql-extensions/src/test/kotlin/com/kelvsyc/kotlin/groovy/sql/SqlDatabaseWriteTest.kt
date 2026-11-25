package com.kelvsyc.kotlin.groovy.sql

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class SqlDatabaseWriteTest : FunSpec({
    test("executeUpdate counts rows") {
        withTestDatabase { ds, _ ->
            SqlDatabase.of(ds).executeUpdate {
                "update books set year = year + 1 where author = ${param("Austen")}"
            } shouldBe 2
        }
    }

    test("executeInsert returns generated keys") {
        withTestDatabase { ds, _ ->
            val keys = SqlDatabase.of(ds).executeInsert(
                sql("insert into books (title, author, year) values (?, ?, ?)", "Ulysses", "Joyce", 1922)
            )
            keys.single().single().toString() shouldBe "4"
        }
    }

    test("execute runs DDL") {
        withTestDatabase { ds, _ ->
            val db = SqlDatabase.of(ds)
            db.execute(trustedSql("create table t (x integer)")) shouldBe false
            db.rows(trustedSql("select count(*) c from t")).single()["c"] shouldBe 0
        }
    }
})
