package uk.co.deftelf.writefocus;

public class CharBuffer implements CharSequence {
    
    private final int DEFAULT_SIZE = 8;
    private final int DEFAULT_INSERT_BUFFER_SIZE = 4;
    
    private char[] chars;
    private int chunk1Size, chunk2Size;
    
    public CharBuffer() {
        chars = new char[DEFAULT_SIZE];
        chunk1Size = 0;
        chunk2Size = 0;
    }
    
    public CharBuffer(CharSequence str) {
        rebase(str);
    }
    
    private void rebase(CharSequence str) {
        chars = new char[str.length() + DEFAULT_INSERT_BUFFER_SIZE];
        chunk1Size = str.length();
        chunk2Size = 0;
        for (int i=0; i < str.length(); i++) 
            chars[i] = str.charAt(i);
    }

    public char charAt(int index) {
        if (index < chunk1Size)
            return chars[index];
        else if (index < (chunk1Size + chunk2Size)) {
            index -= chunk1Size;
            return chars[chars.length - chunk2Size + index];
        }
        return 0;
    }
    
    public void insert(char ch) {
        if ((chunk1Size + chunk2Size) == chars.length)
            rebase(subSequence(0, chars.length));
        
        chars[chunk1Size] = ch;
        chunk1Size++;
        
    }
    
    public void remove() {
        chunk1Size--;
    }
    
    private void moveInsertPointForwards() {
        char ch = chars[chars.length - chunk2Size];
        chunk2Size--;
        chars[chunk1Size] = ch;
        chunk1Size++;
    }
    
    private void moveInsertPointBackwards() {
        char ch = chars[chunk1Size - 1];
        chars[chars.length - chunk2Size - 1] = ch;
        chunk2Size++;
        chunk1Size--;
    }
    
    public void moveInsertPoint(int index) {
        while (index > chunk1Size) {
            moveInsertPointForwards();
            index++;
        }
        while (index < chunk1Size) {
            moveInsertPointBackwards();
            index--;
        }
    }

    public int length() {
        return chunk1Size + chunk2Size;
    }

    public CharSequence subSequence(int start, int end) {
        StringBuilder b = new StringBuilder(end - start);
        for (int i=start; i < end; i++)
            b.append(charAt(i));
        return b;
    }

}
