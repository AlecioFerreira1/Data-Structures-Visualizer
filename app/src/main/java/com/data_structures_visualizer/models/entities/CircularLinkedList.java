package com.data_structures_visualizer.models.entities;

public class CircularLinkedList<T> implements List<T>{
  private Node<T> head;
  private Node<T> tail;
  private int lenght = 0;

  public CircularLinkedList(T startValue){
    if(startValue == null) return;

    createList(new Node<T>(startValue));
  }

  @Override
  public void createList(Node<T> node){
    node.setNext(node);
    head = node;
    tail = node;
    lenght = 1;
  }

  @Override
  public void pushFront(T value){
    if(value == null) return;

    Node<T> node = new Node<T>(value);

    if(head == null){
      createList(node);
      return;
    }

    node.setNext(head);
    head = node;
    tail.setNext(head);
    ++lenght;
  }

  @Override
  public void pushBack(T value){
    if(value == null) return;

    Node<T> node = new Node<T>(value);

    if(head == null){
      createList(node);
      return;
    }

    node.setNext(head);
    tail.setNext(node);
    tail = node;
    ++lenght;
  }

  @Override
  public void print(){
    if(head == null) return;

    System.out.println();

    Node<T> node = head;

    do{
      System.out.printf("%s -> ", node.getValue() != null ? node.getValue().toString() : "null");
      node = node.getNext();
    } while(node != head);

    System.out.println(head == null? null : head.getValue());
  }

  @Override
  public void insertOnPos(T value, int pos){
    if(value == null) return;

    Node<T> node = new Node<T>(value);

    if(head == null){
      createList(node);
      return;
    }

    if(pos == 0){
      node.setNext(head);
      head = node;
      tail.setNext(head);
      ++lenght;
      return;
    }

    Node<T> tmp = head;
    Node<T> tmpPrev = tmp;

    for(int i = 0; i < pos && tmp != null; ++i){
      tmpPrev = tmp;
      tmp = tmp.getNext();
    }

    if(tmp == null){
      tmpPrev.setNext(node);
      tail = node;
      tail.setNext(head);
      return;
    }

    node.setNext(tmp);
    tmpPrev.setNext(node);
    ++lenght;
  }

  @Override
  public int lenght(){
    return lenght;
  }

  @Override
  public void removeItem(int pos){
    if(head == null || pos >= lenght) return;

    if(pos == 0){
      head = head.getNext();
      tail.setNext(head);
      --lenght;
      return;
    }

    Node<T> tmp = head;
    Node<T> tmpPrev = tmp;
  
    for(int i = 0; i < pos && tmp != null; ++i){
      tmpPrev = tmp;
      tmp = tmp.getNext();
    }

    if(tmp == null) return;

    tmpPrev.setNext(tmp.getNext());

    if(tmpPrev.getNext() == null){
      tail = tmpPrev;
      tail.setNext(head);
    }
    
    --lenght;
  }

  @Override
  public void removeItem(T value){
    if(head == null) return;

    if(head.getValue().equals(value)){
      head = head.getNext();
      --lenght;
      return;
    }

    Node<T> tmp = head;
    Node<T> tmpPrev = tmp;
  
    while(tmp != null && !tmp.getValue().equals(value)){
      tmpPrev = tmp;
      tmp = tmp.getNext();
    }

    if(tmp == null) return;

    tmpPrev.setNext(tmp.getNext());

    if(tmpPrev.getNext() == null)
      tail = tmpPrev;
    
    --lenght;
  }

  @Override
  public T get(int index) {
    if(index < 0 || index >= lenght) return null;

    Node<T> target = head;
    int i = index;

    while(i > 0){
      target = target.getNext();
      --i;
    }

    return target != null ? target.getValue() : null;
  }

  @Override
  public boolean isEmpty() {
    return lenght == 0;
  }

  @Override
  public void clear() {
    head = null;
    tail = null;
    lenght = 0;
  }

  @Override
  public int indexOf(T value){
    if(head == null) return -1;

    Node<T> target = head;
    int i = 0;

    while(i < lenght && !target.equals(value)){
      target = target.getNext();
      i++;
    }

    return i == lenght ? -1 : i;
  }
}
