package app.sw.data.repository

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Сериализатор для списков объектов.
 *
 * (сериализатор - преобразователь объекта в JSON)
 *
 * Обертка вокруг встроенного [ListSerializer] Kotlinx Serialization.
 * Используется для сериализации и десериализации списков пользовательских объектов.
 *
 * @param T тип элементов в списке
 * @property elementSerializer сериализатор для элементов списка
 *
 * @sample ActivityRepository.saveActivities
 * @sample ActivityRepository.loadActivities
 *
 * @see KSerializer
 * @see ListSerializer
 */
class ListSerializer<T>(private val elementSerializer: KSerializer<T>) : KSerializer<List<T>> {
    private val delegate = ListSerializer(elementSerializer)

    /**
     * Дескриптор для сериализации списка.
     *
     * @return Дескриптор, описывающий структуру сериализуемого списка
     */
    override val descriptor: SerialDescriptor = delegate.descriptor

    /**
     * Сериализует список в формат JSON.
     *
     * @param encoder кодировщик для преобразования данных
     * @param value список объектов для сериализации
     */
    override fun serialize(encoder: Encoder, value: List<T>) {
        delegate.serialize(encoder, value)
    }

    /**
     * Десериализует список из формата JSON.
     *
     * @param decoder декодировщик для чтения данных
     * @return десериализованный список объектов
     */
    override fun deserialize(decoder: Decoder): List<T> {
        return delegate.deserialize(decoder)
    }
}