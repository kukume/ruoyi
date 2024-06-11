package com.ruoyi.common.hibernate

import org.hibernate.collection.spi.PersistentCollection
import org.hibernate.collection.spi.PersistentSet
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.metamodel.CollectionClassification
import org.hibernate.persister.collection.CollectionPersister
import org.hibernate.usertype.UserCollectionType

class KuKuSetType: UserCollectionType {

    override fun getClassification(): CollectionClassification {
        return CollectionClassification.SET
    }

    override fun getCollectionClass(): Class<*> {
        return Set::class.java
    }

    override fun instantiate(
        session: SharedSessionContractImplementor,
        persister: CollectionPersister
    ): PersistentCollection<*> {
        return KuKuPersistentSet<Any>(session)
    }

    override fun instantiate(anticipatedSize: Int): Any {
        return mutableSetOf<Any>()
    }

    override fun wrap(session: SharedSessionContractImplementor, collection: Any): PersistentCollection<*> {
        return KuKuPersistentSet<Any>(session, collection as Set<Any>)
    }

    override fun getElementsIterator(collection: Any): MutableIterator<*> {
        val set = collection as MutableSet<*>
        return set.iterator()
    }

    override fun contains(collection: Any, entity: Any): Boolean {
        val set = collection as MutableSet<*>
        return set.contains(entity)
    }

    override fun indexOf(collection: Any?, entity: Any?): Any {
        val set = collection as MutableSet<*>
        return set.indexOf(entity)
    }

    override fun replaceElements(
        original: Any?,
        target: Any?,
        persister: CollectionPersister?,
        owner: Any?,
        copyCache: MutableMap<Any?, Any?>?,
        session: SharedSessionContractImplementor?
    ): Any {
        val set = target as MutableSet<Any>
        set.clear()
        set.addAll(original as Set<out Any>)
        return set
    }
}

class KuKuPersistentSet<E>: PersistentSet<E> {
    constructor(session: SharedSessionContractImplementor) : super(session)

    constructor(session: SharedSessionContractImplementor, set: Set<E>) : super(session, set)

    override fun iterator(): MutableIterator<E> {
        return try {
            super.iterator()
        } catch (e: Exception) {
            mutableSetOf<E>().iterator()
        }
    }

    override fun toArray(): Array<Any> {
        return try {
            super.toArray()
        } catch (e: Exception) {
            hashSetOf<Any>().toArray()
        }
    }

    override fun <A : Any?> toArray(array: Array<out A>): Array<A> {
        return try {
            super.toArray(array)
        } catch (e: Exception) {
            hashSetOf<A>().toArray(array)
        }
    }

    override fun containsAll(elements: Collection<E>): Boolean {
        return try {
            super.containsAll(elements)
        } catch (e: Exception) {
            false
        }
    }

    override fun toString(): String {
        return try {
            super.toString()
        } catch (e: Exception) {
            mutableSetOf<E>().toString()
        }
    }

    override fun equals(other: Any?): Boolean {
        return try {
            super.equals(other)
        } catch (e: Exception) {
            false
        }
    }

    override fun hashCode(): Int {
        return try {
            super.hashCode()
        } catch (e: Exception) {
            0
        }
    }

}