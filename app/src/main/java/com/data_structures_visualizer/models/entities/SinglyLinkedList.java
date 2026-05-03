package com.data_structures_visualizer.models.entities;

public class SinglyLinkedList<T> implements List<T>{
  private Node<T> head;
  private Node<T> tail;
  private int lenght = 0;

  public SinglyLinkedList(T startValue){
    if(startValue == null) return;

    createList(new Node<T>(startValue));
  }

  @Override
  public void createList(Node<T> node){
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

    tail.setNext(node);
    tail = node;
    ++lenght;
  }

  @Override
  public void print(){
    if(head == null) return;

    System.out.println();

    for(Node<T> node = head; node != null; node = node.getNext()){
      System.out.printf("%s -> ", node.getValue() != null ? node.getValue().toString() : "null");
    }

    System.out.println("NULL");
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

    if(tmpPrev.getNext() == null)
      tail = tmpPrev;
    
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

    while(i > 0 && target != null){
      target = target.getNext();
      --i;
    }

    if(target == null) return null;
    
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
    Node<T> target = head;
    int i = 0;

    while(target != null && !target.getValue().equals(value)){
      target = target.getNext();
      i++;
    }

    return target == null ? - 1 : i;
  }
}