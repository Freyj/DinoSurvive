﻿package modeles;
/**
 * Classe représentant les Chunks comme divisions du "monde" 
 */
import java.nio.FloatBuffer;
import java.util.Vector;

import org.lwjgl.opengl.GL11;

import modeles.entities.*;
import controleur.Controleur;
import static org.lwjgl.opengl.GL15.*;

public class Chunk {
	private Cube3dVbo[][][] cubes;
	private Vector<Cube3dVbo> renderCubes;
	private Vector<Cube3dVbo> nonRenderCubes;
	private int vboVertexHandleChunk;
	private FloatBuffer vertexData;

	private Controleur clone;
	private int x,y,z,id;

	/**
	 * Constructeur du chunk
	 * @param x
	 * @param y
	 * @param z
	 * @param id
	 * @param contr
	 */
	public Chunk(int x,int y,int z, int id, Controleur contr){
		cubes = new Cube3dVbo[16][16][16];
		renderCubes = new Vector<Cube3dVbo>();
		nonRenderCubes = new Vector<Cube3dVbo>();
		clone = contr;

		this.x = x;
		this.y = y;
		this.z = z;
		//pour l'instant : id = ligne dans le programme, changer ça !!! (extrapoler l'iD des XYZ)
		this.id = id;
	}
	
	public void genVBO(){
		vboVertexHandleChunk = glGenBuffers();
	}

	/**
	 * Renvoie un cube aux coordonnées x,y,z
	 * @param x
	 * @param y
	 * @param z
	 * @return un Cube3dVbo
	 */
	public Cube3dVbo getCube(float x, float y, float z) {
		int tempX = (int)Math.abs(Math.ceil(x))%16;
		int tempY = (int)Math.abs(Math.ceil(y))%16;
		int tempZ = (int)Math.abs(Math.ceil(z))%16;

		return cubes[tempX][tempY][tempZ];
	}

	/*
	 * Methodes
	 */

	/**
	 * Ajoute tous les cube liés à l'ID du chunk (aka la ligne dans le programme)
	 */
	public void addCubes(){
		clone.getMapRead().setCubes(cubes, id);
	}

	/**
	 * Vérifie quels cubes sont actifs (visible ou non) et les met dans la liste de rendu
	 */
	public void checkState(){
		clearChunk();
		for(int i=0; i<16; i++){
			for(int j =0; j<16; j++){
				for(int k=0; k<16; k++){
					if(cubes[i][j][k]!=null){
						if(cubes[i][j][k].getState()){
							renderCubes.add(cubes[i][j][k]);
						}else{
							nonRenderCubes.add(cubes[i][j][k]);
						}
					}
				}
			}
		}
	}

	/**
	 * Met a jours les cubes a rendre ou non
	 */
	public void update(){
		for(int i=0; i<16; i++){
			for(int j =0; j<16; j++){
				for(int k=0; k<16; k++){
					if(cubes[i][j][k]!=null){
						if(surround(i,j,k)){
							cubes[i][j][k].setEtat(false);
						}else{
							cubes[i][j][k].setEtat(true);
						}
					}
				}
			}
		}
	}

	/**
	 * Pour un cube donn�, renvoi si il est visible ou non
	 */
	private boolean surround(int x, int y, int z){
		boolean temp;
		temp = true;
		
		temp = (x>0) ? cubes[x-1][y][z]!=null && temp : false;
		temp = (y>0) ? cubes[x][y-1][z]!=null && temp : false;
		temp = (z>0) ? cubes[x][y][z-1]!=null && temp : false;
		
		temp = (x<15) ? cubes[x+1][y][z]!=null && temp : false;
		temp = (y<15) ? cubes[x][y+1][z]!=null && temp : false;
		temp = (z<15) ? cubes[x][y][z+1]!=null && temp : false;
		
		return  temp;

	}

	/**
	 * Methode générant les cubes dans le buffer
	 * 	DOIT ETRE ASSOCIEE A UNE METHODE DE RESET DES BUFFER !!
	 */
	public void genCubes(){
		for(Cube3dVbo cube : renderCubes){
			cube.genCube();
		}
	}
	
	public void delCubes(){
		for(Cube3dVbo cube : nonRenderCubes){
			cube.delCube(vboVertexHandleChunk);
		}
	}

	/**
	 * Dessine tous les cubes actifs
	 * @param texMan
	 */
	public void draw(TextureManager texMan){
		for(Cube3dVbo cube : renderCubes){
			cube.bindBuffers(vboVertexHandleChunk);

			texMan.genText(cube.getType(), cube.getTextX(), cube.getTextY());
			texMan.bindBuffer();

			cube.bindDrawCube(vboVertexHandleChunk);
			texMan.bindDrawTexture();

			cube.enableCube();
			texMan.enableTexture();

			cube.draw();

			texMan.disableTexture();
			cube.disableCube();
		}
	}

	/**
	 * Vide la liste de rendu
	 */
	public void clearChunk(){
		renderCubes.clear();
		nonRenderCubes.clear();
	}

	/**
	 * Méthode ajoutant un cube dans le chunk
	 * TODO : vérification que le cube a le droit d'être dans ce cube
	 * @param cube
	 */
	public void addCube3dVbo(Cube3dVbo cube){
		int posX = (Math.abs(cube.getX())+((cube.getX()<0)?-1:0))%16;
		int posY = Math.abs(cube.getY())%16;
		int posZ = (Math.abs(cube.getZ())+((cube.getZ()<0)?-1:0))%16;
		
		cubes[posX][posY][posZ] = cube;		
	}

	/*
	 * Getters
	 */
	/**
	 * Renvoie la position du chunk selon les x
	 * @return
	 */
	public int getX(){
		return x;
	}
	/**
	 * Renvoie la position du chunk selon les y
	 * @return
	 */
	public int getY(){
		return y;
	}
	/**
	 * Renvoie la position du chunk selon les z
	 * @return
	 */
	public int getZ(){
		return z;
	}

	/**
	 * Renvoie l'id du chunk
	 * @return
	 */
	public int getID(){
		return id;
	}

}