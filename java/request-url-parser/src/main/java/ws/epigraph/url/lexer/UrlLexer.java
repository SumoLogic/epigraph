/*
 * Copyright 2016 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* The following code was generated by JFlex 1.7.0-SNAPSHOT tweaked for IntelliJ platform */

package ws.epigraph.url.lexer;

import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import static ws.epigraph.url.lexer.UrlElementTypes.*;


/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.7.0-SNAPSHOT
 * from the specification file <tt>UrlLexer.flex</tt>
 */
public class UrlLexer implements FlexLexer {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int YYINITIAL = 0;
  public static final int PARAM_NAME = 2;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = { 
     0,  0,  1, 1
  };

  /** 
   * Translates characters to character classes
   * Chosen bits are [11, 6, 4]
   * Total runtime size is 13728 bytes
   */
  public static int ZZ_CMAP(int ch) {
    return ZZ_CMAP_A[(ZZ_CMAP_Y[(ZZ_CMAP_Z[ch>>10]<<6)|((ch>>4)&0x3f)]<<4)|(ch&0xf)];
  }

  /* The ZZ_CMAP_Z table has 1088 entries */
  static final char ZZ_CMAP_Z[] = zzUnpackCMap(
    "\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\2\11\1\12\1\13\6\14\1\15\23\14\1\16"+
    "\1\14\1\17\1\20\12\14\1\21\10\11\1\22\1\23\1\24\1\25\1\26\1\27\1\30\1\31\1"+
    "\32\1\11\1\33\1\34\2\11\1\14\1\35\3\11\1\36\10\11\1\37\1\40\20\11\1\41\2\11"+
    "\1\42\5\11\1\43\4\11\1\44\1\45\4\11\51\14\1\46\3\14\1\47\1\50\4\14\1\51\12"+
    "\11\1\52\u0381\11");

  /* The ZZ_CMAP_Y table has 2752 entries */
  static final char ZZ_CMAP_Y[] = zzUnpackCMap(
    "\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\2\1\1\10\1\11\1\12\1\13\1\12\1\13\34\12\1"+
    "\14\1\15\1\16\10\1\1\17\1\20\1\12\1\21\4\12\1\22\10\12\1\23\12\12\1\24\1\12"+
    "\1\25\1\24\1\12\1\26\4\1\1\12\1\27\1\30\2\1\2\12\1\27\1\1\1\31\1\24\5\12\1"+
    "\32\1\33\1\34\1\1\1\35\1\12\1\1\1\36\5\12\1\37\1\40\1\41\1\12\1\27\1\42\1"+
    "\12\1\43\1\44\1\1\1\12\1\45\4\1\1\12\1\46\4\1\1\47\2\12\1\50\1\1\1\51\1\52"+
    "\1\24\1\53\1\54\1\55\1\56\1\57\1\60\1\52\1\15\1\61\1\54\1\55\1\62\1\1\1\63"+
    "\1\64\1\65\1\66\1\21\1\55\1\67\1\1\1\70\1\52\1\71\1\72\1\54\1\55\1\67\1\1"+
    "\1\60\1\52\1\40\1\73\1\74\1\75\1\76\1\1\1\70\1\64\1\1\1\77\1\35\1\55\1\50"+
    "\1\1\1\100\1\52\1\1\1\77\1\35\1\55\1\101\1\1\1\57\1\52\1\102\1\77\1\35\1\12"+
    "\1\103\1\57\1\104\1\52\1\105\1\106\1\107\1\12\1\110\1\111\1\1\1\64\1\1\1\24"+
    "\2\12\1\112\1\111\1\113\2\1\1\114\1\115\1\116\1\117\1\120\1\121\2\1\1\70\1"+
    "\1\1\113\1\1\1\122\1\12\1\123\1\1\1\124\7\1\2\12\1\27\1\104\1\113\1\125\1"+
    "\126\1\127\1\130\1\113\2\12\1\131\2\12\1\132\24\12\1\133\1\134\2\12\1\133"+
    "\2\12\1\135\1\136\1\13\3\12\1\136\3\12\1\27\2\1\1\12\1\1\5\12\1\137\1\24\45"+
    "\12\1\140\1\12\1\24\1\27\4\12\1\27\1\141\1\142\1\15\1\12\1\15\1\12\1\15\1"+
    "\142\1\70\3\12\1\143\1\1\1\144\1\113\2\1\1\113\5\12\1\26\2\12\1\145\4\12\1"+
    "\37\1\12\1\146\2\1\1\64\1\12\1\147\1\46\2\12\1\150\1\12\1\76\1\113\2\1\1\12"+
    "\1\111\3\12\1\46\2\1\2\113\1\151\5\1\1\106\2\12\1\143\1\152\1\113\2\1\1\153"+
    "\1\12\1\154\1\41\2\12\1\37\1\1\2\12\1\143\1\1\1\155\1\41\1\12\1\147\6\1\1"+
    "\156\1\157\14\12\4\1\21\12\1\137\2\12\1\137\1\160\1\12\1\147\3\12\1\161\1"+
    "\162\1\163\1\123\1\162\7\1\1\164\1\1\1\123\6\1\1\165\1\166\1\167\1\170\1\171"+
    "\3\1\1\172\147\1\2\12\1\146\2\12\1\146\10\12\1\173\1\174\2\12\1\131\3\12\1"+
    "\175\1\1\1\12\1\111\4\176\4\1\1\104\35\1\1\177\2\1\1\200\1\24\4\12\1\201\1"+
    "\24\4\12\1\132\1\106\1\12\1\147\1\24\4\12\1\146\1\1\1\12\1\27\3\1\1\12\40"+
    "\1\133\12\1\37\4\1\135\12\1\37\2\1\10\12\1\123\4\1\2\12\1\147\20\12\1\123"+
    "\1\12\1\202\1\1\2\12\1\146\1\104\1\12\1\147\4\12\1\37\2\1\1\203\1\204\5\12"+
    "\1\205\1\12\1\147\1\26\3\1\1\203\1\206\1\12\1\30\1\1\3\12\1\143\1\204\2\12"+
    "\1\143\1\1\1\113\1\1\1\207\1\41\1\12\1\37\1\12\1\111\1\1\1\12\1\123\1\47\2"+
    "\12\1\30\1\104\1\113\1\210\1\211\2\12\1\45\1\1\1\212\1\113\1\12\1\213\3\12"+
    "\1\214\1\215\1\216\1\27\1\65\1\217\1\220\1\176\2\12\1\132\1\37\7\12\1\30\1"+
    "\113\72\12\1\143\1\12\1\221\2\12\1\150\20\1\26\12\1\147\6\12\1\76\2\1\1\111"+
    "\1\222\1\55\1\223\1\224\6\12\1\15\1\1\1\153\25\12\1\147\1\1\4\12\1\204\2\12"+
    "\1\26\2\1\1\150\7\1\1\210\7\12\1\123\1\1\1\113\1\24\1\27\1\24\1\27\1\225\4"+
    "\12\1\146\1\226\1\227\2\1\1\230\1\12\1\13\1\231\2\147\2\1\7\12\1\27\30\1\1"+
    "\12\1\123\3\12\1\70\2\1\2\12\1\1\1\12\1\232\2\12\1\37\1\12\1\147\2\12\1\233"+
    "\3\1\11\12\1\147\1\113\5\1\2\12\1\26\3\12\1\143\11\1\23\12\1\111\1\12\1\37"+
    "\1\26\11\1\1\234\2\12\1\235\1\12\1\37\1\12\1\111\1\12\1\146\4\1\1\12\1\236"+
    "\1\12\1\37\1\12\1\76\4\1\3\12\1\237\4\1\1\70\1\240\1\12\1\143\2\1\1\12\1\123"+
    "\1\12\1\123\2\1\1\122\1\12\1\46\1\1\3\12\1\37\1\12\1\37\1\12\1\30\1\12\1\15"+
    "\6\1\4\12\1\45\3\1\3\12\1\30\3\12\1\30\60\1\1\153\2\12\1\26\2\1\1\64\1\1\1"+
    "\153\2\12\2\1\1\12\1\45\1\113\1\153\1\12\1\111\1\64\1\1\2\12\1\241\1\153\2"+
    "\12\1\30\1\242\1\243\2\1\1\12\1\21\1\150\5\1\1\244\1\245\1\45\2\12\1\146\1"+
    "\1\1\113\1\72\1\54\1\55\1\67\1\1\1\246\1\15\21\1\3\12\1\1\1\247\1\113\12\1"+
    "\2\12\1\146\2\1\1\250\2\1\3\12\1\1\1\251\1\113\2\1\2\12\1\27\1\1\1\113\3\1"+
    "\1\12\1\76\1\1\1\113\26\1\4\12\1\113\1\104\34\1\3\12\1\45\20\1\71\12\1\76"+
    "\16\1\14\12\1\143\53\1\2\12\1\146\75\1\44\12\1\111\33\1\43\12\1\45\1\12\1"+
    "\146\1\113\6\1\1\12\1\147\1\1\3\12\1\1\1\143\1\113\1\153\1\252\1\12\67\1\4"+
    "\12\1\46\1\70\3\1\1\153\6\1\1\15\77\1\6\12\1\27\1\123\1\45\1\76\66\1\5\12"+
    "\1\210\3\12\1\142\1\253\1\254\1\255\3\12\1\256\1\257\1\12\1\260\1\261\1\35"+
    "\24\12\1\262\1\12\1\35\1\132\1\12\1\132\1\12\1\210\1\12\1\210\1\146\1\12\1"+
    "\146\1\12\1\55\1\12\1\55\1\12\1\263\3\264\14\12\1\46\123\1\1\255\1\12\1\265"+
    "\1\266\1\267\1\270\1\271\1\272\1\273\1\150\1\274\1\150\24\1\55\12\1\111\2"+
    "\1\103\12\1\46\15\12\1\147\150\12\1\15\25\1\41\12\1\147\36\1");

  /* The ZZ_CMAP_A table has 3024 entries */
  static final char ZZ_CMAP_A[] = zzUnpackCMap(
    "\11\0\2\1\1\0\2\1\22\0\1\1\1\47\1\0\1\46\2\0\1\51\1\4\1\52\1\53\1\3\1\11\1"+
    "\35\1\26\1\13\1\2\12\10\1\34\1\36\1\54\1\37\1\55\1\50\1\45\4\24\1\14\25\24"+
    "\1\42\1\5\1\43\1\0\1\25\1\27\1\21\1\6\1\24\1\33\1\17\1\20\2\24\1\30\2\24\1"+
    "\22\1\31\1\56\1\24\1\32\1\24\1\16\1\23\1\15\1\7\5\24\1\40\1\0\1\41\1\44\13"+
    "\0\1\24\12\0\1\24\4\0\1\24\5\0\27\24\1\0\12\24\4\0\14\24\16\0\5\24\7\0\1\24"+
    "\1\0\1\24\1\0\5\24\1\0\2\24\2\0\4\24\1\0\1\24\6\0\1\24\1\0\3\24\1\0\1\24\1"+
    "\0\4\24\1\0\23\24\1\0\13\24\10\0\6\24\1\0\26\24\2\0\1\24\6\0\10\24\10\0\13"+
    "\24\5\0\3\24\15\0\12\12\4\0\6\24\1\0\1\24\17\0\2\24\7\0\2\24\12\12\3\24\2"+
    "\0\2\24\1\0\16\24\15\0\11\24\13\0\1\24\16\0\12\12\6\24\4\0\2\24\4\0\1\24\5"+
    "\0\6\24\4\0\1\24\11\0\1\24\3\0\1\24\7\0\11\24\7\0\5\24\17\0\26\24\3\0\1\24"+
    "\2\0\1\24\7\0\12\24\4\0\12\12\1\24\4\0\10\24\2\0\2\24\2\0\26\24\1\0\7\24\1"+
    "\0\1\24\3\0\4\24\3\0\1\24\20\0\1\24\15\0\2\24\1\0\1\24\5\0\6\24\4\0\2\24\1"+
    "\0\2\24\1\0\2\24\1\0\2\24\17\0\4\24\1\0\1\24\7\0\12\12\2\0\3\24\20\0\11\24"+
    "\1\0\2\24\1\0\2\24\1\0\5\24\3\0\1\24\2\0\1\24\30\0\1\24\13\0\10\24\2\0\1\24"+
    "\3\0\1\24\1\0\6\24\3\0\3\24\1\0\4\24\3\0\2\24\1\0\1\24\1\0\2\24\3\0\2\24\3"+
    "\0\3\24\3\0\14\24\13\0\10\24\1\0\2\24\10\0\3\24\5\0\4\24\1\0\5\24\3\0\1\24"+
    "\3\0\2\24\15\0\13\24\2\0\1\24\21\0\1\24\12\0\6\24\5\0\22\24\3\0\10\24\1\0"+
    "\11\24\1\0\1\24\2\0\7\24\11\0\1\24\1\0\2\24\14\0\12\12\7\0\2\24\1\0\1\24\2"+
    "\0\2\24\1\0\1\24\2\0\1\24\6\0\4\24\1\0\7\24\1\0\3\24\1\0\1\24\1\0\1\24\2\0"+
    "\2\24\1\0\4\24\1\0\2\24\11\0\1\24\2\0\5\24\1\0\1\24\11\0\12\12\2\0\14\24\1"+
    "\0\24\24\13\0\5\24\3\0\6\24\4\0\4\24\3\0\1\24\3\0\2\24\7\0\3\24\4\0\15\24"+
    "\14\0\1\24\1\0\6\24\1\0\1\24\5\0\1\24\2\0\13\24\1\0\15\24\1\0\4\24\2\0\7\24"+
    "\1\0\1\24\1\0\4\24\2\0\1\24\1\0\4\24\2\0\7\24\1\0\1\24\1\0\4\24\2\0\16\24"+
    "\2\0\6\24\2\0\15\24\2\0\1\24\1\0\10\24\7\0\15\24\1\0\6\24\23\0\1\24\4\0\1"+
    "\24\3\0\11\24\1\0\1\24\5\0\17\24\1\0\16\24\2\0\14\24\13\0\1\24\15\0\7\24\7"+
    "\0\16\24\15\0\2\24\12\12\3\0\3\24\11\0\4\24\1\0\4\24\3\0\2\24\11\0\10\24\1"+
    "\0\1\24\1\0\1\24\1\0\1\24\1\0\6\24\1\0\7\24\1\0\1\24\3\0\3\24\1\0\7\24\3\0"+
    "\4\24\2\0\6\24\5\0\1\24\15\0\1\24\2\0\1\24\4\0\1\24\2\0\12\24\1\0\1\24\3\0"+
    "\5\24\6\0\1\24\1\0\1\24\1\0\1\24\1\0\4\24\1\0\13\24\2\0\4\24\5\0\5\24\4\0"+
    "\1\24\4\0\2\24\13\0\5\24\6\0\4\24\3\0\2\24\14\0\10\24\7\0\10\24\1\0\7\24\6"+
    "\0\2\24\12\0\5\24\5\0\2\24\3\0\7\24\6\0\3\24\12\12\2\24\13\0\11\24\2\0\27"+
    "\24\2\0\7\24\1\0\3\24\1\0\4\24\1\0\4\24\2\0\6\24\3\0\1\24\1\0\1\24\2\0\5\24"+
    "\1\0\12\24\12\12\5\24\1\0\3\24\1\0\10\24\4\0\7\24\3\0\1\24\3\0\2\24\1\0\1"+
    "\24\3\0\2\24\2\0\5\24\2\0\1\24\1\0\1\24\30\0\3\24\3\0\6\24\2\0\6\24\2\0\6"+
    "\24\11\0\7\24\4\0\5\24\3\0\5\24\5\0\1\24\1\0\10\24\1\0\5\24\1\0\1\24\1\0\2"+
    "\24\1\0\2\24\1\0\12\24\6\0\12\24\2\0\6\24\2\0\6\24\2\0\6\24\2\0\3\24\3\0\14"+
    "\24\1\0\16\24\1\0\2\24\1\0\2\24\1\0\10\24\6\0\4\24\4\0\16\24\2\0\1\24\1\0"+
    "\14\24\1\0\2\24\3\0\1\24\2\0\4\24\1\0\2\24\12\0\10\24\6\0\6\24\1\0\3\24\1"+
    "\0\12\24\3\0\1\24\12\0\4\24\13\0\12\12\1\24\1\0\1\24\3\0\7\24\1\0\1\24\1\0"+
    "\4\24\1\0\17\24\1\0\2\24\14\0\3\24\4\0\2\24\1\0\1\24\20\0\4\24\10\0\1\24\13"+
    "\0\10\24\5\0\3\24\2\0\1\24\2\0\2\24\2\0\4\24\1\0\14\24\1\0\1\24\1\0\7\24\1"+
    "\0\21\24\1\0\4\24\2\0\10\24\1\0\7\24\1\0\14\24\1\0\4\24\1\0\5\24\1\0\1\24"+
    "\3\0\14\24\2\0\13\24\1\0\10\24\2\0\22\12\1\0\2\24\1\0\1\24\2\0\1\24\1\0\12"+
    "\24\1\0\4\24\1\0\1\24\1\0\1\24\6\0\1\24\4\0\1\24\1\0\1\24\1\0\1\24\1\0\3\24"+
    "\1\0\2\24\1\0\1\24\2\0\1\24\1\0\1\24\1\0\1\24\1\0\1\24\1\0\1\24\1\0\2\24\1"+
    "\0\1\24\2\0\4\24\1\0\7\24\1\0\4\24\1\0\4\24\1\0\1\24\1\0\12\24\1\0\5\24\1"+
    "\0\3\24\1\0\5\24\1\0\5\24");

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\2\0\1\1\1\2\1\3\1\4\1\1\1\5\1\6"+
    "\1\7\1\10\3\5\1\11\2\1\2\5\1\12\1\13"+
    "\1\14\1\15\1\16\1\17\1\20\1\21\1\22\1\23"+
    "\1\24\1\25\1\26\1\27\1\30\1\31\1\32\1\33"+
    "\1\5\1\34\1\35\1\36\1\0\1\37\3\0\1\6"+
    "\3\5\1\0\4\5\1\0\1\36\1\0\1\6\1\0"+
    "\3\5\1\40\2\5\1\0\1\36\1\0\1\41\1\42"+
    "\1\5\1\43\1\0\1\5\1\0\1\5\1\44";

  private static int [] zzUnpackAction() {
    int [] result = new int[78];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\57\0\136\0\215\0\274\0\136\0\353\0\u011a"+
    "\0\u0149\0\u0178\0\u01a7\0\u01d6\0\u0205\0\u0234\0\136\0\u0178"+
    "\0\u0263\0\u0292\0\u02c1\0\136\0\136\0\136\0\136\0\136"+
    "\0\136\0\136\0\136\0\136\0\136\0\136\0\136\0\136"+
    "\0\136\0\136\0\136\0\136\0\136\0\u02f0\0\u031f\0\136"+
    "\0\u034e\0\353\0\136\0\u037d\0\u01a7\0\u03ac\0\u03db\0\u040a"+
    "\0\u0439\0\u0468\0\u0263\0\136\0\u0497\0\u04c6\0\u04f5\0\u0524"+
    "\0\u0553\0\u0582\0\u05b1\0\u05b1\0\u05e0\0\u060f\0\u063e\0\u011a"+
    "\0\u066d\0\u069c\0\u06cb\0\136\0\u06fa\0\u011a\0\u011a\0\u0729"+
    "\0\u011a\0\u0758\0\u0787\0\u07b6\0\u07e5\0\u011a";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[78];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\3\1\4\1\5\1\6\1\7\1\3\2\10\1\11"+
    "\1\12\1\11\1\13\1\10\1\14\2\10\1\15\1\10"+
    "\1\16\2\10\1\17\1\20\1\21\1\10\1\22\1\10"+
    "\1\23\1\24\1\25\1\26\1\27\1\30\1\31\1\32"+
    "\1\33\1\34\1\35\1\36\1\37\1\40\1\41\1\42"+
    "\1\43\1\44\1\45\1\46\6\3\3\47\1\3\1\47"+
    "\1\3\11\47\3\3\4\47\3\3\1\50\16\3\1\47"+
    "\60\0\1\4\60\0\1\51\53\0\4\52\1\53\1\54"+
    "\51\52\6\0\3\10\1\0\1\10\1\0\11\10\3\0"+
    "\4\10\22\0\1\10\10\0\1\11\1\0\1\11\1\55"+
    "\1\56\2\0\1\56\47\0\1\11\1\0\1\11\1\55"+
    "\53\0\1\57\1\0\1\57\52\0\3\10\1\0\1\10"+
    "\1\0\2\10\1\60\6\10\3\0\4\10\22\0\1\10"+
    "\6\0\3\10\1\0\1\10\1\0\5\10\1\61\3\10"+
    "\3\0\4\10\22\0\1\10\6\0\3\10\1\0\1\10"+
    "\1\0\11\10\3\0\1\62\3\10\22\0\1\10\27\63"+
    "\1\64\27\63\6\0\3\10\1\0\1\10\1\0\5\10"+
    "\1\65\3\10\3\0\4\10\22\0\1\10\6\0\3\10"+
    "\1\0\1\10\1\0\3\10\1\66\5\10\3\0\4\10"+
    "\22\0\1\10\6\0\1\10\1\67\1\10\1\0\1\10"+
    "\1\0\11\10\3\0\4\10\22\0\1\10\4\0\1\70"+
    "\1\0\3\47\1\0\1\47\1\0\11\47\3\0\4\47"+
    "\22\0\1\47\3\51\1\71\53\51\2\0\1\52\1\0"+
    "\3\52\1\72\5\0\2\52\1\0\1\52\35\0\1\52"+
    "\10\0\1\73\1\74\1\73\13\0\1\74\40\0\1\57"+
    "\1\0\1\57\1\0\1\56\2\0\1\56\45\0\1\10"+
    "\1\75\1\10\1\0\1\10\1\0\11\10\3\0\4\10"+
    "\22\0\1\10\6\0\3\10\1\0\1\10\1\0\6\10"+
    "\1\76\2\10\3\0\4\10\22\0\1\10\6\0\3\10"+
    "\1\0\1\10\1\0\7\10\1\77\1\10\3\0\4\10"+
    "\22\0\1\10\6\0\3\10\1\0\1\10\1\0\11\10"+
    "\3\0\2\10\1\100\1\10\22\0\1\10\6\0\3\10"+
    "\1\0\1\10\1\0\4\10\1\101\4\10\3\0\4\10"+
    "\22\0\1\10\6\0\3\10\1\0\1\10\1\0\6\10"+
    "\1\102\2\10\3\0\4\10\22\0\1\10\25\0\2\103"+
    "\30\0\2\51\1\104\1\71\53\51\10\0\1\105\56\0"+
    "\1\73\1\0\1\73\52\0\3\10\1\0\1\10\1\0"+
    "\3\10\1\106\5\10\3\0\4\10\22\0\1\10\6\0"+
    "\3\10\1\0\1\10\1\0\7\10\1\75\1\10\3\0"+
    "\4\10\22\0\1\10\6\0\3\10\1\0\1\10\1\0"+
    "\1\10\1\107\7\10\3\0\4\10\22\0\1\10\6\0"+
    "\3\10\1\0\1\10\1\0\5\10\1\110\3\10\3\0"+
    "\4\10\22\0\1\10\6\0\3\10\1\0\1\10\1\0"+
    "\6\10\1\111\2\10\3\0\4\10\22\0\1\10\4\0"+
    "\1\47\62\0\1\112\54\0\1\10\1\113\1\10\1\0"+
    "\1\10\1\0\11\10\3\0\4\10\22\0\1\10\10\0"+
    "\1\114\54\0\3\10\1\0\1\10\1\0\6\10\1\115"+
    "\2\10\3\0\4\10\22\0\1\10\10\0\1\52\54\0"+
    "\3\10\1\0\1\10\1\0\1\10\1\116\7\10\3\0"+
    "\4\10\22\0\1\10";

  private static int [] zzUnpackTrans() {
    int [] result = new int[2068];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String[] ZZ_ERROR_MSG = {
    "Unknown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\2\0\1\11\2\1\1\11\10\1\1\11\4\1\22\11"+
    "\2\1\1\11\1\1\1\0\1\11\3\0\4\1\1\0"+
    "\1\11\3\1\1\0\1\1\1\0\1\1\1\0\6\1"+
    "\1\0\1\11\1\0\4\1\1\0\1\1\1\0\2\1";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[78];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private CharSequence zzBuffer = "";

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /**
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean zzEOFDone;

  /* user code: */
  public UrlLexer() {
    this((java.io.Reader)null);
  }


  /**
   * Creates a new scanner
   *
   * @param   in  the java.io.Reader to read input from.
   */
  public UrlLexer(java.io.Reader in) {
    this.zzReader = in;
  }


  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    int size = 0;
    for (int i = 0, length = packed.length(); i < length; i += 2) {
      size += packed.charAt(i);
    }
    char[] map = new char[size];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < packed.length()) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }

  public final int getTokenStart() {
    return zzStartRead;
  }

  public final int getTokenEnd() {
    return getTokenStart() + yylength();
  }

  public void reset(CharSequence buffer, int start, int end, int initialState) {
    zzBuffer = buffer;
    zzCurrentPos = zzMarkedPos = zzStartRead = start;
    zzAtEOF  = false;
    zzAtBOL = true;
    zzEndRead = end;
    yybegin(initialState);
  }

  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   *
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {
    return true;
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final CharSequence yytext() {
    return zzBuffer.subSequence(zzStartRead, zzMarkedPos);
  }


  /**
   * Returns the character at position <tt>pos</tt> from the
   * matched text.
   *
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch.
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer.charAt(zzStartRead+pos);
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of
   * yypushback(int) and a match-all fallback rule) this method
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  }


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public IElementType advance() throws java.io.IOException {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    CharSequence zzBufferL = zzBuffer;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;

      zzState = ZZ_LEXSTATE[zzLexicalState];

      // set up zzAction for empty match case:
      int zzAttributes = zzAttrL[zzState];
      if ( (zzAttributes & 1) == 1 ) {
        zzAction = zzState;
      }


      zzForAction: {
        while (true) {

          if (zzCurrentPosL < zzEndReadL) {
            zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL/*, zzEndReadL*/);
            zzCurrentPosL += Character.charCount(zzInput);
          }
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL/*, zzEndReadL*/);
              zzCurrentPosL += Character.charCount(zzInput);
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + ZZ_CMAP(zzInput) ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
        zzAtEOF = true;
        return null;
      }
      else {
        switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
          case 1: 
            { return com.intellij.psi.TokenType.BAD_CHARACTER;
            }
          case 37: break;
          case 2: 
            { return com.intellij.psi.TokenType.WHITE_SPACE;
            }
          case 38: break;
          case 3: 
            { return U_SLASH;
            }
          case 39: break;
          case 4: 
            { return U_STAR;
            }
          case 40: break;
          case 5: 
            { return U_ID;
            }
          case 41: break;
          case 6: 
            { return U_NUMBER;
            }
          case 42: break;
          case 7: 
            { return U_PLUS;
            }
          case 43: break;
          case 8: 
            { return U_DOT;
            }
          case 44: break;
          case 9: 
            { return U_UNDERSCORE;
            }
          case 45: break;
          case 10: 
            { return U_COLON;
            }
          case 46: break;
          case 11: 
            { return U_COMMA;
            }
          case 47: break;
          case 12: 
            { return U_SEMICOLON;
            }
          case 48: break;
          case 13: 
            { return U_EQ;
            }
          case 49: break;
          case 14: 
            { return U_CURLY_LEFT;
            }
          case 50: break;
          case 15: 
            { return U_CURLY_RIGHT;
            }
          case 51: break;
          case 16: 
            { return U_BRACKET_LEFT;
            }
          case 52: break;
          case 17: 
            { return U_BRACKET_RIGHT;
            }
          case 53: break;
          case 18: 
            { return U_TILDA;
            }
          case 54: break;
          case 19: 
            { return U_AT;
            }
          case 55: break;
          case 20: 
            { return U_HASH;
            }
          case 56: break;
          case 21: 
            { return U_BANG;
            }
          case 57: break;
          case 22: 
            { yybegin(PARAM_NAME); return U_QMARK;
            }
          case 58: break;
          case 23: 
            { yybegin(PARAM_NAME); return U_AMP;
            }
          case 59: break;
          case 24: 
            { return U_PAREN_LEFT;
            }
          case 60: break;
          case 25: 
            { return U_PAREN_RIGHT;
            }
          case 61: break;
          case 26: 
            { return U_ANGLE_LEFT;
            }
          case 62: break;
          case 27: 
            { return U_ANGLE_RIGHT;
            }
          case 63: break;
          case 28: 
            { return U_PARAM_NAME;
            }
          case 64: break;
          case 29: 
            { yybegin(YYINITIAL); return U_EQ;
            }
          case 65: break;
          case 30: 
            { return U_BLOCK_COMMENT;
            }
          case 66: break;
          case 31: 
            { return U_STRING;
            }
          case 67: break;
          case 32: 
            { return U_MAP;
            }
          case 68: break;
          case 33: 
            { return U_BOOLEAN;
            }
          case 69: break;
          case 34: 
            { return U_LIST;
            }
          case 70: break;
          case 35: 
            { return U_NULL;
            }
          case 71: break;
          case 36: 
            { return U_DEFAULT;
            }
          case 72: break;
          default:
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}
