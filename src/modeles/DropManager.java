package modeles;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_COORD_ARRAY;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glTexCoordPointer;
import static org.lwjgl.opengl.GL11.glVertexPointer;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glClientActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import modeles.entities.FlatItemVBO;

public class DropManager {

	private ArrayList<FlatItemVBO> drops;
	protected FloatBuffer interleavedBuffer;
	private int vboVertexHandleDrop;

	public DropManager(){
		drops = new ArrayList<FlatItemVBO>();
	}

	public void addDrop(FlatItemVBO item){
		drops.add(item);
	}
	
	public void removeDrop(FlatItemVBO item){
		drops.remove(item);
	}
	
	public void draw(DropTextureManager texMan){	
		glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandleDrop);
		glEnableClientState(GL_VERTEX_ARRAY);
		glVertexPointer(3, GL_FLOAT, 5*4, 0L);
		glClientActiveTexture(GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texMan.getID());
		glEnableClientState(GL_TEXTURE_COORD_ARRAY);
		glTexCoordPointer(2, GL_FLOAT, 5*4, 3*4 );
		glDrawArrays(GL_TRIANGLES, 0, drops.size()*6*2);
		glDisableClientState(GL_VERTEX_ARRAY);
		glDisableClientState(GL_TEXTURE_COORD_ARRAY);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
	
	public void gen(DropTextureManager texMan){
		genDrop(texMan);
		genVBO();
	}

	private void genDrop(DropTextureManager texMan){
		if(!drops.isEmpty()){
			float cubeCoord[],texCoord[];
			interleavedBuffer = BufferUtils.createFloatBuffer(drops.size()*((9*3+9*2)*4));
			int j=0;
			
			for(FlatItemVBO item : drops){
				
				cubeCoord=item.genCubes();
				texCoord=texMan.genText(item.getCoord().x, item.getCoord().y);
				
				for(int i = 0; i< cubeCoord.length; i+=3){
					interleavedBuffer.put(cubeCoord[i]);
					interleavedBuffer.put(cubeCoord[i+1]);
					interleavedBuffer.put(cubeCoord[i+2]);
					
					interleavedBuffer.put(texCoord[j]);
					interleavedBuffer.put(texCoord[j+1]);

					j+=2;
				}
				j=0;
			}
		}
		interleavedBuffer.flip();
	}
	
	private void genVBO(){
		if(!drops.isEmpty()){
			vboVertexHandleDrop = GL15.glGenBuffers();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboVertexHandleDrop);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, interleavedBuffer, GL15.GL_STATIC_DRAW);

			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		}
	}
	
	public void unbind(){
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboVertexHandleDrop);
		GL15.glDeleteBuffers(vboVertexHandleDrop);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		if(interleavedBuffer!=null){
			interleavedBuffer.clear();
		}
	}
}