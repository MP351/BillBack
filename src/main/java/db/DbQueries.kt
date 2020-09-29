package db

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID

interface DbQueries<in WEB, out T: Entity<Int>> {
    fun add(entity: WEB): EntityID<Int>
    fun getAll(): List<T>
    fun getById(id: Int): T
    fun updateById(id: Int, entity: WEB)
    fun deleteById(id: Int)
}