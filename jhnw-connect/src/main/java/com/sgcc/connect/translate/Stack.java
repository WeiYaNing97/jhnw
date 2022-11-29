package com.sgcc.connect.translate;

/**
 * @author 天幕顽主
 * @E-mail: WeiYaNing97@163.com
 * @date 2022年03月24日 14:03
 */
/**

 * @Project: struts2

 * @Title: Stack.java

 * @Package com.yza.struct

 * @author yongzhian

 * @date 2014-10-8 下午2:49:10

 * @Copyright: 2014 www.yineng.com.cn Inc. All rights reserved.

 * @version V1.0

 */

/**

 * @ClassName Stack

 * @Description 堆栈

 * @author yongzhian

 * @Date 2014-10-8

 */

public class Stack {
    int index = -1;

    int size;

    Object[] objArr;

    Object o;

    boolean isEmpty;

    public Stack() {
        this(100);

    }

    public Stack(int size) {
        this.size = size;

        if (size <= 0) {
            System.out.println("堆栈初始化错误!");

        } else {
            this.size = size;

            objArr = new Object[this.size];

        }

    }

// 添加元素 入栈

    public void push(Object o) {
        if (++index != size) {
            objArr[index] = o;

        } else {// 如果超过了原始堆栈的大小则会将最下面的压出去

            for (int i = 0; i < size - 1; i++) {
                objArr[i] = objArr[i + 1];

            }

            if (index-- == 0) {
                index = 0;

            }

            objArr[index] = objArr;

        }

    }

// 得到出栈的对象

    public Object pop() {
        if (index != 0) {
            o = objArr[index];

            objArr[index--] = null;

        } else {
            o = objArr[0];

            objArr[0] = null;

        }

        return o;

    }

    public boolean isEmpty() {
        isEmpty = false;

        if (objArr[0] == null) {
            isEmpty = true;
        }

        return isEmpty;

    }

    public static void main(String[] args) {
        Stack stack = new Stack();

        stack.push("先进");
        stack.push("后进");

        Object pop = stack.pop();

        System.out.println("\r\n1 = "+pop);
        pop = stack.pop();

        System.out.println("\r\n2 = "+pop);

    }

}