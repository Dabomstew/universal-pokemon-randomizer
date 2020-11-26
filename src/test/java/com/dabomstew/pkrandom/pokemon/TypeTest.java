package com.dabomstew.pkrandom.pokemon;

import static org.junit.Assert.*;
import java.util.Random;
import java.util.Arrays;
import org.junit.Test;

public class TypeTest {    
    private Pokemon prior = new Pokemon(), latter = new Pokemon();
    private Type priorInitial = Type.FIRE;
    private Type latterInitial = Type.DARK;
    private Type sharedType = Type.GROUND;
    private Type sharedType2 = Type.BUG;
    private Type newType;

    public Type randomType(Type[] excluded) {
        Type newType = Type.randomType(new Random());
        while(excluded != null && Arrays.stream(excluded).anyMatch(newType::equals)) {
            newType = Type.randomType(new Random());
        }
        return newType;
    }
    
    @Test
    public void TestRandomTypeCarryZeroTypesDiffer() {
     // ******************
        // Types are the same
        // ******************
        int typesDiffer = 0;
        prior.primaryType = sharedType;
        prior.secondaryType = sharedType2;
        latter.primaryType = sharedType2;
        latter.secondaryType = sharedType;
        
        // Prior first type changes
        Type newType = randomType(new Type[] {sharedType2});
        prior.primaryType = newType;
        prior.typeChanged = 1;
        latter.assignTypeByReference(prior, typesDiffer, () -> randomType(null));
        assertEquals(latter.typeChanged, 1);
        assertEquals(latter.primaryType, newType);
        assertNotEquals(latter.primaryType, latter.secondaryType);
        assertEquals(latter.secondaryType, prior.secondaryType);
        assertNotEquals(latter.secondaryType, null);
        
        // Prior second type changes
        newType = randomType(new Type[] {newType});
        prior.secondaryType = newType;
        prior.typeChanged = 2;
        latter.assignTypeByReference(prior, typesDiffer, () -> randomType(null));
        assertEquals(latter.typeChanged, 2);
        assertEquals(latter.primaryType, prior.primaryType);
        assertNotEquals(latter.primaryType, latter.secondaryType);
        assertEquals(latter.secondaryType, newType);
        assertNotEquals(latter.secondaryType, null);
    }
    
    @Test
    public void TestRandomTypeCarryPrimaryTypesDiffer() {  
        // ******************
        // Primary Types differ
        // ******************
        int typesDiffer = 1;
        prior.primaryType = priorInitial;
        prior.secondaryType = sharedType;
        latter.primaryType = latterInitial;
        latter.secondaryType = sharedType;
        
        // Prior first type changes
        newType = randomType(new Type[] {prior.secondaryType, latterInitial});
        prior.primaryType = newType;
        prior.typeChanged = 1;
        latter.assignTypeByReference(prior, typesDiffer, () -> randomType(null));
        assertEquals(latter.typeChanged, 1);
        assertNotEquals(latter.primaryType, latter.secondaryType);
        assertEquals(latter.secondaryType, prior.secondaryType);
        assertNotEquals(latter.secondaryType, null);
        
        // Prior second type changes
        prior.primaryType = priorInitial;
        latter.primaryType = latterInitial;
        newType = randomType(new Type[] {prior.primaryType, latterInitial});
        prior.secondaryType = newType;
        prior.typeChanged = 2;
        latter.assignTypeByReference(prior, typesDiffer, () -> randomType(null));
        assertEquals(latter.typeChanged, 2);
        assertEquals(latter.primaryType, latterInitial);
        assertNotEquals(latter.primaryType, latter.secondaryType);
        assertEquals(latter.secondaryType, prior.secondaryType);
        assertEquals(latter.secondaryType, newType);
        assertNotEquals(latter.secondaryType, null);
        
        // Prior primary type changes into latter initial
        prior.primaryType = latterInitial;
        prior.secondaryType = sharedType;
        latter.primaryType = latterInitial;
        latter.secondaryType = sharedType;
        prior.typeChanged = 1;
        latter.assignTypeByReference(prior, typesDiffer, () -> randomType(null));
        assertEquals(latter.typeChanged, 1);
        assertNotEquals(latter.primaryType, latter.secondaryType);
        assertEquals(latter.secondaryType, prior.secondaryType);
        assertEquals(latter.secondaryType, sharedType);
        assertNotEquals(latter.secondaryType, null);
        
        // Prior secondary type changes into latter initial
        prior.primaryType = priorInitial;
        prior.secondaryType = latterInitial;
        latter.primaryType = latterInitial;
        latter.secondaryType = sharedType;
        prior.typeChanged = 2;
        latter.assignTypeByReference(prior, typesDiffer, () -> randomType(null));
        assertEquals(latter.typeChanged, 2);
        assertEquals(latter.primaryType, prior.secondaryType);
        assertNotEquals(latter.primaryType, latter.secondaryType);
        assertNotEquals(latter.secondaryType, null);
    }
    
    @Test
    public void TestRandomTypeCarrySecondaryTypesDiffer() {
        // ******************
        // Secondary Types differ        
        // ******************
        int typesDiffer = 2;
        prior.primaryType = sharedType;
        prior.secondaryType = priorInitial;
        latter.primaryType = sharedType;
        latter.secondaryType = latterInitial;
        
        // Prior first type changes
        newType = randomType(new Type[] {prior.secondaryType, latterInitial});
        prior.primaryType = newType;
        prior.typeChanged = 1;
        latter.assignTypeByReference(prior, typesDiffer, () -> randomType(null));
        assertEquals(latter.typeChanged, 1);
        assertNotEquals(latter.primaryType, latter.secondaryType);
        assertEquals(latter.primaryType, prior.primaryType);
        assertEquals(latter.primaryType , newType);
        assertEquals(latter.secondaryType, latterInitial);
        assertNotEquals(latter.secondaryType, null);
        
        // Prior second type changes
        prior.primaryType = sharedType;
        latter.primaryType = sharedType;
        newType = randomType(new Type[] {prior.primaryType, latterInitial});
        prior.secondaryType = newType;
        prior.typeChanged = 2;
        latter.assignTypeByReference(prior, typesDiffer, () -> randomType(null));
        assertEquals(latter.typeChanged, 2);
        assertEquals(latter.primaryType, sharedType);
        assertNotEquals(latter.primaryType, latter.secondaryType);
        assertEquals(latter.primaryType, prior.primaryType);
        assertNotEquals(latter.secondaryType, null);
        
        // Prior primary type changes into latter initial
        prior.primaryType = latterInitial;
        prior.secondaryType = priorInitial;
        latter.primaryType = sharedType;
        latter.secondaryType = latterInitial;
        prior.typeChanged = 1;
        latter.assignTypeByReference(prior, typesDiffer, () -> randomType(null));
        assertEquals(latter.typeChanged, 1);
        assertNotEquals(latter.primaryType, prior.primaryType);
        assertNotEquals(latter.primaryType, latter.secondaryType);
        assertEquals(latter.secondaryType, prior.primaryType);
        assertEquals(latter.secondaryType, latterInitial);
        assertNotEquals(latter.secondaryType, null);
        
        // Prior secondary type changes into latter initial
        prior.primaryType = sharedType;
        prior.secondaryType = latterInitial;
        latter.primaryType = sharedType;
        latter.secondaryType = latterInitial;
        prior.typeChanged = 2;
        latter.assignTypeByReference(prior, typesDiffer, () -> randomType(null));
        assertEquals(latter.typeChanged, 2);
        assertEquals(latter.primaryType, prior.primaryType);
        assertNotEquals(latter.primaryType, latter.secondaryType);
        assertEquals(latter.primaryType, sharedType);
        assertNotEquals(latter.secondaryType, null);
        

        // Latter has no secondary type and prior primary changes
        newType = randomType(new Type[] {prior.secondaryType, latterInitial});
        prior.primaryType = newType;
        prior.secondaryType = priorInitial;
        latter.primaryType = sharedType;
        latter.secondaryType = null;
        prior.typeChanged = 1;
        latter.assignTypeByReference(prior, typesDiffer, () -> randomType(null));
        assertEquals(latter.typeChanged, 1);
        assertEquals(latter.primaryType, prior.primaryType);
        assertNotEquals(latter.primaryType, latter.secondaryType);
        assertEquals(latter.primaryType, newType);
        assertEquals(latter.secondaryType, null);
        
        // Latter has no secondary type and prior secondary changes
        newType = randomType(new Type[] {prior.secondaryType, latterInitial});
        prior.primaryType = sharedType;
        prior.secondaryType = newType;
        latter.primaryType = sharedType;
        latter.secondaryType = null;
        prior.typeChanged = 2;
        latter.assignTypeByReference(prior, typesDiffer, () -> randomType(null));
        assertEquals(latter.typeChanged, 1);
        assertEquals(latter.primaryType, prior.secondaryType);
        assertNotEquals(latter.primaryType, latter.secondaryType);
        assertEquals(latter.primaryType, newType);
        assertEquals(latter.secondaryType, null);
        
        // Latter has no secondary type and prior secondary changes
        // and no types are shared
        // WEEPINBELL -> SNORLAX
        // RNG Seed: 113088671701646
        // Settings: 181AREBAQgYAIQAAABylAaoAkcCKgGUFAARCQT+AAAAAAAACWjaDBorAQMSUG9rZW1vbiBZZWxsb3cgKFUpbBtkiuPDOIo=
        newType = Type.FIGHTING;
        prior.primaryType = Type.GRASS;
        prior.secondaryType = newType;
        latter.primaryType = Type.NORMAL;
        latter.secondaryType = null;
        prior.typeChanged = 2;
        latter.assignTypeByReference(prior, typesDiffer, () -> randomType(null));
        assertEquals(latter.typeChanged, 1);
        assertEquals(latter.primaryType, prior.secondaryType);
        assertNotEquals(latter.primaryType, latter.secondaryType);
        assertEquals(latter.primaryType, newType);
        assertEquals(latter.secondaryType, null);
    }

    @Test
    public void TestRandomTypeCarrySecondaryAndPrimaryTypesDiffer() {
        // ******************
        // Secondary and Primary Types differ        
        // ******************
        int typesDiffer = 3;
        prior.primaryType = sharedType;
        prior.secondaryType = priorInitial;
        latter.primaryType = latterInitial;
        latter.secondaryType = sharedType;
        
        // Prior first type changes
        newType = randomType(new Type[] {prior.secondaryType, latterInitial});
        prior.primaryType = newType;
        prior.typeChanged = 1;
        latter.assignTypeByReference(prior, typesDiffer, () -> randomType(null));
        assertEquals(latter.typeChanged, 2);
        assertNotEquals(latter.primaryType, latter.secondaryType);
        assertEquals(latter.primaryType, latterInitial);
        assertEquals(latter.secondaryType, newType);
        assertEquals(latter.secondaryType, prior.primaryType);
        assertNotEquals(latter.secondaryType, null);
        
        // Prior second type changes
        prior.primaryType = sharedType;
        latter.secondaryType = sharedType;
        newType = randomType(new Type[] {prior.secondaryType, latterInitial});
        prior.secondaryType = newType;
        prior.typeChanged = 2;
        latter.assignTypeByReference(prior, typesDiffer, () -> randomType(null));
        assertEquals(latter.typeChanged, 1);
        assertNotEquals(latter.primaryType, latter.secondaryType);
        assertEquals(latter.secondaryType, sharedType);
        assertEquals(latter.secondaryType, prior.primaryType);
        assertNotEquals(latter.secondaryType, null);
        
        // Prior primary type changes into latter initial
        prior.primaryType = latterInitial;
        prior.secondaryType = priorInitial;
        latter.primaryType = latterInitial;
        latter.secondaryType = sharedType;
        prior.typeChanged = 1;
        latter.assignTypeByReference(prior, typesDiffer, () -> randomType(null));
        assertEquals(latter.typeChanged, 2);
        assertEquals(latter.primaryType, prior.primaryType);
        assertEquals(latter.primaryType, latterInitial);
        assertNotEquals(latter.primaryType, latter.secondaryType);
        assertNotEquals(latter.secondaryType, null);
        
        // Prior secondary type changes into latter initial
        prior.primaryType = sharedType;
        prior.secondaryType = latterInitial;
        latter.primaryType = latterInitial;
        latter.secondaryType = sharedType;
        prior.typeChanged = 2;
        latter.assignTypeByReference(prior, typesDiffer, () -> randomType(null));
        assertEquals(latter.typeChanged, 1);        
        assertNotEquals(latter.primaryType, latter.secondaryType);
        assertEquals(latter.secondaryType, sharedType);
        assertEquals(latter.secondaryType, prior.primaryType);
        assertNotEquals(latter.secondaryType, null);
    }
        
    @Test
    public void TestRandomTypeCarryPrimaryAndSecondaryTypesDiffer() {
        // ******************
        // Primary and Secondary Types differ        
        // ******************
        int typesDiffer = 4;
        prior.primaryType = priorInitial;
        prior.secondaryType = sharedType;
        latter.primaryType = sharedType;
        latter.secondaryType = latterInitial;
        
        // Prior first type changes
        newType = randomType(new Type[] {prior.secondaryType, latterInitial});
        prior.primaryType = newType;
        prior.typeChanged = 1;
        latter.assignTypeByReference(prior, typesDiffer, () -> randomType(null));
        assertEquals(latter.typeChanged, 2);
        assertNotEquals(latter.primaryType, latter.secondaryType);
        assertEquals(latter.primaryType, prior.secondaryType);
        assertEquals(latter.primaryType , sharedType);
        assertNotEquals(latter.secondaryType, null);
        
        // Prior second type changes
        prior.primaryType = priorInitial;
        latter.secondaryType = latterInitial;
        newType = randomType(new Type[] {prior.primaryType, latterInitial});
        prior.secondaryType = newType;
        prior.typeChanged = 2;
        latter.assignTypeByReference(prior, typesDiffer, () -> randomType(null));
        assertEquals(latter.typeChanged, 1);
        assertNotEquals(latter.primaryType, latter.secondaryType);
        assertEquals(latter.primaryType, prior.secondaryType);
        assertEquals(latter.secondaryType, latterInitial);
        assertNotEquals(latter.secondaryType, null);
        
        // Prior primary type changes into latter initial
        prior.primaryType = latterInitial;
        prior.secondaryType = sharedType;
        latter.primaryType = sharedType;
        latter.secondaryType = latterInitial;
        prior.typeChanged = 1;
        latter.assignTypeByReference(prior, typesDiffer, () -> randomType(null));
        assertEquals(latter.typeChanged, 2);
        assertNotEquals(latter.primaryType, latter.secondaryType);
        assertEquals(latter.primaryType, prior.secondaryType);
        assertEquals(latter.primaryType, sharedType);
        assertNotEquals(latter.secondaryType, null);
        
        // Prior secondary type changes into latter initial
        prior.primaryType = priorInitial;
        prior.secondaryType = latterInitial;
        latter.primaryType = sharedType;
        latter.secondaryType = latterInitial;
        prior.typeChanged = 2;
        latter.assignTypeByReference(prior, typesDiffer, () -> randomType(null));
        assertEquals(latter.typeChanged, 1);
        assertNotEquals(latter.primaryType, latter.secondaryType);
        assertEquals(latter.secondaryType, latterInitial);
        assertEquals(latter.secondaryType, prior.secondaryType);
        

        // Latter has no secondary type and prior primary changes
        newType = randomType(new Type[] {prior.secondaryType, latterInitial});
        prior.primaryType = newType;
        prior.secondaryType = sharedType;
        latter.primaryType = sharedType;
        latter.secondaryType = null; 
        prior.typeChanged = 1;
        latter.assignTypeByReference(prior, typesDiffer, () -> randomType(null));
        assertEquals(latter.typeChanged, 1);
        assertEquals(latter.primaryType, prior.primaryType);
        assertNotEquals(latter.primaryType, latter.secondaryType);
        assertEquals(latter.primaryType, newType);
        assertEquals(latter.secondaryType, null);
        
        // Latter has no secondary type and prior secondary changes
        newType = randomType(new Type[] {prior.secondaryType, latterInitial});
        prior.primaryType = priorInitial;
        prior.secondaryType = newType;
        latter.primaryType = sharedType;
        latter.secondaryType = null;
        prior.typeChanged = 2;
        latter.assignTypeByReference(prior, typesDiffer, () -> randomType(null));
        assertEquals(latter.typeChanged, 1);
        assertEquals(latter.primaryType, prior.secondaryType);
        assertNotEquals(latter.primaryType, latter.secondaryType);
        assertEquals(latter.primaryType, newType);
        assertEquals(latter.secondaryType, null);
    }
}
