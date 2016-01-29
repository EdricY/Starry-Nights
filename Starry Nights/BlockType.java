
/**
 * Enumeration class BlockType - write a description of the enum class here
 * 
 * @author (your name here)
 * @version (version number or date here)
 */
public enum BlockType
    {
        AIR (0,0),
        GRASS (1,1),
        DIRT (12,1),
        STONE (1,12),
        SAND (23,1),
        ICE (12,12),
        SNOW (23,12),
        STAR(34,1);
        private int x, y;
        BlockType(int x, int y){
            this.x = x;
            this.y = y;
        }
        public int getX(){return x;}
        public int getY(){return y;}
    }
