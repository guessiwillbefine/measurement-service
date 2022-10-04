package vadim.andreich.util.convert;

import java.util.List;

@FunctionalInterface
public interface Converter<T,V> {
    T convert (V original);
}
