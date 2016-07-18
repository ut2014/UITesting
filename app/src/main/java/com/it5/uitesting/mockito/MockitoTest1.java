package com.it5.uitesting.mockito;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;



/**
 * Created by IT5 on 2016/7/18.
 */
public class MockitoTest1 {
    /**
     * 通过when(mock.someMethod()).thenReturn(value) 来设定mock对象某个方法调用时的返回值
     */
    @Test
    public void simpleTest() {
        // arrange
        Iterator i = mock(Iterator.class);
        when(i.next()).thenReturn("Hello").thenReturn("World");
        // act
        String result = i.next() + " " + i.next();
        // 验证i.next()是否被调用了2次，不关心返回值
        verify(i, times(2)).next();
        // 断言结果是否和预期一样
        assertEquals("Hello World", result);
    }

    /**
     * 参数匹配器--Argument matchers test.
     */
    @Test
    public void argumentMatchersTest() {
        List<String> mock = mock(List.class);
        // anyInt()参数匹配器来匹配任何的int 类型的参数
        when(mock.get(anyInt())).thenReturn("Hello").thenReturn("World");

        // 所以当第一次调用get方法时输入任意参数为100方法返回”Hello”，第二次调用时输入任意参数200返回值”World”。
        String result = mock.get(100) + " " + mock.get(200);

        // verfiy 验证的时候也可将参数指定为anyInt()匹配器，那么它将不关心调用时输入的参数的具体参数值。
        verify(mock, times(2)).get(anyInt());
        assertEquals("Hello World", result);
        /* 注意：如果使用了参数匹配器，那么所有的参数需要由匹配器来提供，否则将会报错。 */
    }

    /**
     * 参数匹配器--Argument matchers test2.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void argumentMatchersTest2() {
        Map<Integer, String> mapMock = mock(Map.class);
        when(mapMock.put(anyInt(), anyString())).thenReturn("world");
        mapMock.put(1, "hello");
        // 注：在最后的验证时如果只输入字符串”hello”是会报错的，必须使用Matchers 类内建的eq方法。
        // 如果将anyInt()换成1进行验证也需要用eq(1)。
        verify(mapMock).put(anyInt(), eq("hello"));
    }

    /**
     * 3.Mock对象的行为验证--Verify test.
     */
    @Test
    public void verifyTest() {
        List<String> mock = mock(List.class);
        List<String> mock1 = mock(List.class);

        when(mock.get(0)).thenReturn("hello");
        mock.get(0);
        mock.get(1);
        mock.get(2);
        mock1.get(0);
        /* 方法的调用不关心是否模拟了get(2)方法的返回值，只关心mock 对象后，是否执行了mock.get(2)，如果没有执行，测试方法将不会通过。 */
        verify(mock).get(2); // 验证对象mock是否调用了get(2)方法
        verify(mock, never()).get(3); // 方法中可以传入never()方法参数来确认mock.get(3)方法不曾被执行过
        /* 确认mock1对象没有进行任何交互===>测试不通过 */
        verifyNoMoreInteractions(mock1); // 将其放在 mock1.get(0);之前即可通过。
    }

    /**
     * 验证方法的调用顺序.
     */
    @Test
    public void testInvokeOrder() {
        List<String> firstMock = mock(List.class);
        List<String> secondMock = mock(List.class);

        firstMock.add("was called first");
        firstMock.add("was called first");
        secondMock.add("was called second");
        secondMock.add("was called third");

        /* 如果mock方法的调用顺序和InOrder中verify的顺序不同，那么测试将执行失败。 */

        InOrder inOrder = inOrder(secondMock, firstMock);
        inOrder.verify(firstMock, times(2)).add("was called first");
        inOrder.verify(secondMock).add("was called second");
        // 因为在secondMock.add("was called third")之后已经没有多余的方法调用了。
        inOrder.verify(secondMock).add("was called third");
        inOrder.verifyNoMoreInteractions();// 表示此方法调用后再没有多余的交互
    }

    /**
     * 自定义Answer接口（方法预期回调接口）的应用
     */

    @Test
    public void customAnswerTest() {
        List<String> mock = mock(List.class);
        when(mock.get(4)).thenAnswer(new Answer() {
            public String answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Integer num = (Integer) args[0];
                if (num > 3) {
                    return "yes";
                }
                throw new RuntimeException();
            }
        });
        System.out.println(mock.get(4));
    }

    /**
     * 利用ArgumentCaptor（参数捕获器）捕获方法参数进行验证
     */
    @Test
    public void argumentCaptorTest() {
        List mock = mock(List.class);
        List mock2 = mock(List.class);
        mock.add("John");
        mock2.add("Brian");
        mock2.add("Jim");
        /*
         * 首先构建ArgumentCaptor需要传入捕获参数的对象，例子中是String。接着要在 verify 方法的参数中调用argument.capture()方法来捕获输入的参数， <br> 之后
         * argument变量中就保存了参数值，可以用argument.getValue()获取。
         */
        ArgumentCaptor argument = ArgumentCaptor.forClass(String.class);
        verify(mock).add(argument.capture());
        assertEquals("John", argument.getValue());
        verify(mock2, times(2)).add(argument.capture());

        assertEquals("Jim", argument.getValue());
        /* argument.getAllValues()，它将返回参数值的List。 */
        assertArrayEquals(new Object[] { "John", "Brian", "Jim" }, argument.getAllValues().toArray());
    }

    /**
     * Spy-对象的监视<br>
     * Mock 对象只能调用stubbed 方法，调用不了它真实的方法。但Mockito 可以监视一个真实的对象，这时对它进行方法调用时它将调用真实的方法，<br>
     * 同时也可以stubbing 这个对象的方法让它返回我们的期望值。另外不论是否是真实的方法调用都可以进行verify验证。<br>
     * 和创建mock对象一样，对于final类、匿名类和Java的基本类型是无法进行spy的。
     */
    @Test
    public void spyTest2() {
        List list = new LinkedList();
        List spy = spy(list);
        // optionally, you can stub out some methods:
        when(spy.size()).thenReturn(100);
        // using the spy calls real methods
        spy.add("one");
        spy.add("two");
        // prints "one" - the first element of a list
        System.out.println(spy.get(0));
        // size() method was stubbed - 100 is printed
        System.out.println(spy.size());
        // optionally, you can verify
        verify(spy).add("one");
        verify(spy).add("two");
    }

}
