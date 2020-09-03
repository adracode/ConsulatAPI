package fr.leconsulat.api.nbt;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

public final class NBTOutputStream implements Closeable {
    
    private final DataOutputStream os;
    private final CompoundTag compoundTag;
    
    public NBTOutputStream(final File file, CompoundTag compoundTag) throws IOException{
        this(new FileOutputStream(file), compoundTag);
    }
    
	public NBTOutputStream(final OutputStream os, CompoundTag compoundTag) throws IOException{
		this(os, true, compoundTag);
	}
    
    public NBTOutputStream(OutputStream os, final boolean gzipped, CompoundTag compoundTag) throws IOException{
        if(gzipped){
            os = new GZIPOutputStream(os);
        }
        this.os = new DataOutputStream(os);
        this.compoundTag = compoundTag;
    }
	
	public void write(String name) throws IOException{
		writeTag(name, compoundTag);
	}
    
    /**
     * Ecris un Tag
     *
     * @param tag The tag to write.
     * @throws IOException if an I/O error occurs.
     */
    private void writeTag(String name, Tag tag) throws IOException{
        NBTType type = tag.getType();
        byte[] nameBytes = name.getBytes(NBTConstants.CHARSET);
        os.writeByte(type.getId());
        os.writeShort(nameBytes.length);
        os.write(nameBytes);
        if(type == NBTType.END){
            throw new IOException("[JNBT] Named TAG_End not permitted.");
        }
        writeTag(tag);
    }
    
    /**
     * Writes tag payload.
     *
     * @param tag The tag.
     * @throws IOException if an I/O error occurs.
     */
    private void writeTag(final Tag tag) throws IOException{
        NBTType type = tag.getType();
        switch(type){
            case END:
                writeEndTag();
                break;
            case BYTE:
                writeByteTag((NumberTag)tag);
                break;
            case SHORT:
                writeShortTag((NumberTag)tag);
                break;
            case INT:
                writeIntTag((NumberTag)tag);
                break;
            case LONG:
                writeLongTag((NumberTag)tag);
                break;
            case FLOAT:
                writeFloatTag((NumberTag)tag);
                break;
            case DOUBLE:
                writeDoubleTag((NumberTag)tag);
                break;
            case BYTE_ARRAY:
                writeByteArrayTag((ByteArrayTag)tag);
                break;
            case STRING:
                writeStringTag((StringTag)tag);
                break;
            case LIST:
                writeListTag((ListTag<? extends Tag>)tag);
                break;
            case COMPOUND:
                writeCompoundTag((CompoundTag)tag);
                break;
            case INT_ARRAY:
                writeIntArrayTag((IntArrayTag)tag);
                break;
            default:
                throw new IOException("[JNBT] Invalid tag type: " + type + ".");
        }
    }
    
    private void writeByteTag(NumberTag tag) throws IOException{
        os.writeByte(tag.getByte());
    }
    
    private void writeByteArrayTag(final ByteArrayTag tag) throws IOException{
        final byte[] bytes = tag.getValue();
        os.writeInt(bytes.length);
        os.write(bytes);
    }
    
    private void writeCompoundTag(final CompoundTag tag) throws IOException{
        for(Map.Entry<String, Tag> childTag : tag.getValue().entrySet()){
            writeTag(childTag.getKey(), childTag.getValue());
        }
         writeEndTag();
    }
    
    private void writeListTag(final ListTag<? extends Tag> tag) throws IOException{
        List<? extends Tag> tags = tag.getValue();
        int size = tags.size();
        os.writeByte(tag.getElementType().getId());
        os.writeInt(size);
		for(Tag value : tags){
			writeTag(value);
		}
    }
    
    private void writeStringTag(final StringTag tag) throws IOException{
        final byte[] bytes = tag.getValue().getBytes(NBTConstants.CHARSET);
        os.writeShort(bytes.length);
        os.write(bytes);
    }
    
    private void writeDoubleTag(NumberTag tag) throws IOException{
        os.writeDouble(tag.getDouble());
    }
    
    private void writeFloatTag(NumberTag tag) throws IOException{
        os.writeFloat(tag.getFloat());
    }
    
    private void writeLongTag(NumberTag tag) throws IOException{
        os.writeLong(tag.getLong());
    }
    
    private void writeIntTag(NumberTag tag) throws IOException{
        os.writeInt(tag.getInt());
    }
    
    private void writeShortTag(NumberTag tag) throws IOException{
        os.writeShort(tag.getShort());
    }
  
    private void writeIntArrayTag(IntArrayTag tag) throws IOException{
        final int[] ints = tag.getValue();
        os.writeInt(ints.length);
		for(int i : ints){
			os.writeInt(i);
		}
    }
    
    private void writeEndTag() throws IOException{
		os.writeByte((byte)0);
    }
    
    @Override
    public void close() throws IOException{
        os.close();
    }
}
