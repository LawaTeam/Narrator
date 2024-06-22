package me.sakuratao.narrator.spigot.utils;

import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@UtilityClass
public class ObjectUtil {

    /**
     * 对对象列表进行分页。
     *
     * @param objectList 待分页的对象列表。
     * @param volume 每页包含的对象数量。
     * @return 分页后的对象列表列表。
     */
    public List<List<Object>> paginateObjects(List<Object> objectList, int volume) {
        // 使用流将对象列表分页
        return IntStream.range(0, objectList.size())
                .boxed()
                .collect(Collectors.groupingBy(i -> i / volume))
                .values()
                .stream()
                .map(indices -> indices
                        .stream() // 将索引列表转换为流
                        .map(objectList::get) // 根据索引获取对象列表中的对象
                        .toList() // 将流转换为列表
                )
                .toList(); // 将所有页的对象列表收集到一个列表中
    }


}
