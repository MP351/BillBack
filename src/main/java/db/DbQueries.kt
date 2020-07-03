package db

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID

interface DbQueries<in H, out T: IntEntity> {
    fun add(entity: H): EntityID<Int>
    fun getAll(): List<T>
    fun getById(id: Int): T
    fun updateById(id: Int, entity: H)
    fun deleteById(id: Int)
}