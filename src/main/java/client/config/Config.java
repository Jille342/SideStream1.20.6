/*    */ package client.config;
/*    */ 
/*    */ import client.Client;
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ public abstract class Config
/*    */ {
/*    */   private final File file;
/*    */   private final String name;
/*    */   
/*    */   public Config(String name) {
/* 14 */     this.name = name;
/* 15 */     this.file = new File(Client.FOLDER, String.valueOf(name) + ".json");
/* 16 */     if (!this.file.exists()) {
/*    */       try {
/* 18 */         this.file.createNewFile();
/* 19 */       } catch (IOException e) {
/* 20 */         e.printStackTrace();
/*    */       } 
/* 22 */       save();
/*    */     } 
/*    */   }
/*    */   
/*    */   public final File getFile() {
/* 27 */     return this.file;
/*    */   }
/*    */   
/*    */   public final String getName() {
/* 31 */     return this.name;
/*    */   }
/*    */   
/*    */   public abstract void load();
/*    */   
/*    */   public abstract void save();
/*    */ }


/* Location:              C:\Users\Null\Downloads\Qurobito LEAK\Qurobito.jar!\client\config\Config.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */