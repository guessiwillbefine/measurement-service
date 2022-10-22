package vadim.andreich.util.convert;

@FunctionalInterface
public interface Converter<T,V> {
    T convert (V original);
}
