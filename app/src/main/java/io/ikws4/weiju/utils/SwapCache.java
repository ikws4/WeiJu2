package io.ikws4.weiju.utils;

public class SwapCache<T> {
   private T front;
   private T back;

   public SwapCache(T first, T second) {
      this.front = first;
      this.back = second;
   }

   public void swap() {
      T temp = front;
      front = back;
      back = temp;
   }

   public T getFront() {
      return front;
   }

   public T getBack() {
      return front;
   }
}
