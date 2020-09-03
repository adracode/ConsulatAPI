package fr.leconsulat.api.nbt;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public final class NBTInputStream implements Closeable {
    
    private final DataInputStream is;
    
    public NBTInputStream(final File file) throws IOException{
        this(new FileInputStream(file));
    }
    
    public NBTInputStream(final DataInputStream is){
        this.is = is;
    }
    
    public NBTInputStream(final InputStream is) throws IOException{
        this(is, true);
    }
    
    public NBTInputStream(InputStream is, final boolean gzipped) throws IOException{
        if(gzipped){
            is = new GZIPInputStream(is);
        }
        this.is = new DataInputStream(is);
    }
    
    public CompoundTag read() throws IOException{
        return (CompoundTag)readTag(0).tag;
    }
    
    /**
     * Lis un tag NBT
     * <p>
     * Un Tag NBT est composé
     * d'un octet pour le type
     * de deux octets pour la taille du nom
     * du nom
     * de la charge utile (information)
     *
     * @param depth The depth of this tag.
     * @return The tag that was read.
     * @throws IOException if an I/O error occurs.
     */
    private NamedTag readTag(final int depth) throws IOException{
        NBTType type = readType();
        String name = type != NBTType.END ? readName() : "";
        return new NamedTag(name, readTagPayload(type, depth));
    }
    
    /**
     * Lis la charge utile d'un Tag
     *
     * @param type  The type.
     * @param depth The depth.
     * @return The tag.
     * @throws IOException if an I/O error occurs.
     */
    private Tag readTagPayload(NBTType type, int depth) throws IOException{
        switch(type){
            case END:
                if(depth == 0){
                    throw new IOException("[JNBT] TAG_End found without a TAG_Compound/TAG_List tag preceding it.");
                } else {
                    return EndTag.TAG;
                }
            case BYTE:
                return new ByteTag(is.readByte());
            case SHORT:
                return new ShortTag(is.readShort());
            case INT:
                return new IntTag(is.readInt());
            case LONG:
                return new LongTag(is.readLong());
            case FLOAT:
                return new FloatTag(is.readFloat());
            case DOUBLE:
                return new DoubleTag(is.readDouble());
            case BYTE_ARRAY:
                byte[] bytes = new byte[is.readInt()];
                is.readFully(bytes);
                return new ByteArrayTag(bytes);
            case STRING:
                bytes = new byte[is.readShort()];
                is.readFully(bytes);
                return new StringTag(new String(bytes, NBTConstants.CHARSET));
            case LIST:
                NBTType childType = readType();
                List<Tag> tagList = new ArrayList<>();
                for(int i = 0, length = is.readInt(); i < length; i++){
                    Tag tag = readTagPayload(childType, depth + 1);
                    if(tag instanceof EndTag){
                        throw new IOException("[JNBT] TAG_End not permitted in a list.");
                    }
                    tagList.add(tag);
                }
                return new ListTag<>(childType, tagList);
            case COMPOUND:
                CompoundTag compoundTag = new CompoundTag();
                while(true){
                    NamedTag namedTag = readTag(depth + 1);
                    Tag tag = namedTag.tag;
                    if(tag instanceof EndTag){
                        break;
                    } else {
                        compoundTag.put(namedTag.name, tag);
                    }
                }
                return compoundTag;
            case INT_ARRAY:
                int length = is.readInt();
                final int[] ints = new int[length];
                for(int i = 0; i < length; i++){
                    ints[i] = is.readInt();
                }
                return new IntArrayTag(ints);
            default:
                throw new IOException("[JNBT] Invalid tag type: " + type + ".");
        }
    }
    
    private NBTType readType() throws IOException{
        return NBTType.byId(is.readByte() & 0xFF);
    }
    
    private short readNameLength() throws IOException{
        return (short)(is.readShort() & 0xFFFF);
    }
    
    private String readName() throws IOException{
        //Récupère la taille du nom (unsigned) et créer le tableau qui va contenir le nom
        byte[] nameBytes = new byte[readNameLength()];
        //Lis le nom
        is.readFully(nameBytes);
        return new String(nameBytes, NBTConstants.CHARSET);
    }
    
    @Override
    public void close() throws IOException{
        is.close();
    }
    
    private static class NamedTag {
        
        private String name;
        private Tag tag;
    
        public NamedTag(String name, Tag tag){
            this.name = name;
            this.tag = tag;
        }
    }
    
}
