package uk.co.deftelf.writefocus;

public class CharBuffer implements CharSequence {
    
    private final int DEFAULT_SIZE = 1024;
    private final int DEFAULT_INSERT_BUFFER_SIZE = 64;
    
    private char[] chars;
    private int chunk1Size, chunk2Size;
    private int chunk2Start;
    
    public CharBuffer() {
        chars = new char[DEFAULT_SIZE];
        chunk1Size = 0;
        chunk2Size = 0;
        chunk2Start = 0;
    }
    
    public CharBuffer(String str) {
        chars = new char[str.length() + DEFAULT_INSERT_BUFFER_SIZE];
        chunk1Size = str.length();
        chunk2Size = 0;
        chunk2Start = str.length();
        for (int i=0; i < str.length(); i++) 
            chars[i] = str.charAt(i);
    }

    public char charAt(int index) {
        if (index < chunk1Size)
            return chars[index];
        else if (index < (chunk1Size + chunk2Size)) {
            index -= chunk1Size;
            return chars[chunk2Start + index];
        }
        return 0;
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
