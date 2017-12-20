package org.hisoka.stream;

import org.hisoka.stream.entity.Book;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Hinsteny
 * @date 2017-12-20
 * @copyright: 2017 All rights reserved.
 */
public class CollectorsGroupTest {

    // 短格式时间
    private static final String shortDate = "yyyyMMdd";

    @DataProvider(name = "getSimpleBooksList")
    public Object[][] getSimpleBooksList() {
        List<Book> books = Book.getBooks("Notre-Dame de Paris", "朝花夕拾", "朝花夕拾", "挪威的森林");

        return new Object[][]{{books}};
    }

    @DataProvider(name = "getBooksList")
    public Object[][] getBooksList() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("Notre-Dame de Paris", 86.9, parseDate(shortDate, "18310101")));
        books.add(new Book("朝花夕拾", 36.8, parseDate(shortDate, "19260211")));
        books.add(new Book("朝花夕拾", 36.8, parseDate(shortDate, "19260211")));
        books.add(new Book("挪威的森林", 30.5, parseDate(shortDate, "19870101")));

        return new Object[][]{{books}};
    }

    /**
     * 测试集合分组
     * @param books
     */
    @Test(dataProvider = "getSimpleBooksList")
    public void testGroupByName(List<Book> books) {
        // 按照书名分组
        Map<String, List<Book>> collect_one = books.stream().collect(Collectors.groupingBy((book) -> book.getName()));
        System.out.println(collect_one);
        Assert.assertEquals(collect_one.size(), 3, "分组数应该是3");
        // 按照书名分组, 但是要去重
        Map<String, Set<Book>> collect_two = books.stream().collect(Collectors.groupingBy((book) -> book.getName(), Collectors.toSet()));
        Assert.assertEquals(collect_two.size(), 3, "分组数应该是3");
    }

    /**
     * 集合分组后统计
     * @param books
     */
    @Test(dataProvider = "getBooksList")
    public void groupByNameCount(List<Book> books) {
        // 按书名分组, 统计每种数的总本书
        Map<String, Integer> count = books.stream().collect(Collectors.groupingBy(book -> book.getName(), Collectors.summingInt(t -> 1)));
        Assert.assertEquals(count.size(), 3, "应该有3种书");
        // 按书名分组, 统计每种数的总价格
        Map<String, Double> count_2 = books.stream().collect(Collectors.groupingBy(book -> book.getName(), Collectors.summingDouble(book -> book.getPrice())));
        Assert.assertEquals(count_2.get("朝花夕拾"), 36.8*2, "朝花夕拾 总价不对");
        // 按照某个时间点进行分割, 统计二者总数
        Date split = parseDate(shortDate, "14000101");
        Map<Boolean, Integer> count_3 = books.stream().collect(Collectors.partitioningBy(book -> book.getTime().before(split), Collectors.summingInt(t -> 1)));
        Assert.assertEquals(count_3.size(), 2, "只有两个分类");
    }

    /**
     * 求一些分析值, 最大, 最小, 平均
     * @param books
     */
    @Test(dataProvider = "getBooksList")
    public void calculateStreams(List<Book> books) {
        // 找出最晚发版的著作
        Comparator<Book> byLastPublish = Comparator.comparing((book -> book.getTime()));
        Optional<Book> book = books.stream().collect(Collectors.minBy(byLastPublish));
        Assert.assertEquals(book.get().getName(), "Notre-Dame de Paris", "Notre-Dame de Paris 是最早发布的书");
        // 找出书价最高的书
//        Comparator<Book> byMaxPrice = Comparator.comparing((_book -> -_book.getPrice()));
        Comparator<Book> byMaxPrice = Collections.reverseOrder(Comparator.comparing((_book -> _book.getPrice())));
        book = books.stream().collect(Collectors.minBy(byMaxPrice));
        Assert.assertEquals(book.get().getName(), "Notre-Dame de Paris", "Notre-Dame de Paris 是价格最高的书");
        // 算出所有书的平均书价
        Double averagePrice = books.stream().collect(Collectors.averagingDouble((__book -> __book.getPrice())));
        Assert.assertEquals(averagePrice, 47.75, "评价书价有误");
    }

    /**
     * 解析时间字符串
     *
     * @param format
     * @param dateStr
     * @return
     */
    public static Date parseDate(String format, String dateStr) {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat(format);
        try {
            date = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

}
