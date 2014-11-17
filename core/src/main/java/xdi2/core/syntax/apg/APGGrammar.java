package xdi2.core.syntax.apg;

import com.coasttocoastresearch.apg.Grammar;
import java.io.PrintStream;

public class APGGrammar extends Grammar{

    // public API
    public static Grammar getInstance(){
        if(factoryInstance == null){
            factoryInstance = new APGGrammar(getRules(), getUdts(), getOpcodes());
        }
        return factoryInstance;
    }

    // rule name enum
    public static int ruleCount = 101;
    public enum RuleNames{
        ARC_ADDRESS("arc-address", 18, 95, 3),
        ATTL("ATTL", 62, 275, 1),
        ATTR("ATTR", 63, 276, 1),
        ATTR_ADDRESS("attr-address", 16, 89, 2),
        ATTR_CLASS("attr-class", 58, 263, 5),
        ATTR_COLL_NONVAR("attr-coll-nonvar", 56, 255, 4),
        ATTR_COLL_VAR("attr-coll-var", 54, 247, 4),
        ATTR_COLLECTION("attr-collection", 53, 244, 3),
        ATTR_DEFN("attr-defn", 57, 259, 4),
        ATTR_MEMBER("attr-member", 59, 268, 5),
        ATTR_MEMBER_CONTEXT("attr-member-context", 10, 67, 6),
        ATTR_PART("attr-part", 26, 134, 6),
        ATTR_PART_CONTEXT("attr-part-context", 9, 60, 7),
        ATTR_SINGLETON("attr-singleton", 52, 240, 4),
        ATTR_UNIT("attr-unit", 25, 129, 5),
        ATTR_VAR("attr-var", 55, 251, 4),
        AUTH_VAR_COLL("auth-var-coll", 39, 190, 4),
        AUTHORITY_COLLECTION("authority-collection", 41, 198, 4),
        AUTHORITY_SINGLETON("authority-singleton", 31, 158, 4),
        AUTHORITY_VARIABLE("authority-variable", 28, 146, 4),
        C_I("C-I", 69, 282, 1),
        CLASS_COLLECTION("class-collection", 42, 202, 3),
        CLASS_DEFINITION("class-definition", 30, 154, 4),
        CLASS_SINGLETON("class-singleton", 32, 162, 3),
        CLASS_VAR_COLL("class-var-coll", 40, 194, 4),
        CLASS_VARIABLE("class-variable", 29, 150, 4),
        COLL("COLL", 60, 273, 1),
        COLR("COLR", 61, 274, 1),
        COMMON_ADDRESS("common-address", 14, 84, 3),
        COMMON_ARC_ADDRESS("common-arc-address", 19, 98, 9),
        CONTEXT_SYMBOL("context-symbol", 80, 348, 7),
        CONTEXTUAL_STATEMENT("contextual-statement", 2, 12, 8),
        DEFL("DEFL", 64, 277, 1),
        DEFR("DEFR", 65, 278, 1),
        DIGIT("DIGIT", 84, 360, 1),
        ENDLINE("ENDLINE", 87, 363, 6),
        ENTITY_ADDRESS("entity-address", 15, 87, 2),
        ENTITY_COLLECTION("entity-collection", 38, 185, 5),
        ENTITY_MEMBER("entity-member", 48, 225, 1),
        ENTITY_MEMBER_CONTEXT("entity-member-context", 8, 52, 8),
        ENTITY_PART("entity-part", 24, 123, 6),
        ENTITY_PART_CONTEXT("entity-part-context", 7, 43, 9),
        ENTITY_SINGLETON("entity-singleton", 27, 140, 6),
        ENTITY_UNIT("entity-unit", 23, 118, 5),
        GENERAL_COLLECTION("general-collection", 45, 213, 4),
        GENERAL_SINGLETON("general-singleton", 35, 173, 4),
        HEX("HEX", 83, 357, 3),
        INNER_ROOT("inner-root", 22, 112, 6),
        INNER_ROOT_CONTEXT("inner-root-context", 6, 38, 5),
        IRI_BODY("iri-body", 90, 380, 7),
        IRI_CHAR("iri-char", 91, 387, 4),
        IRI_DELIM("iri-delim", 92, 391, 8),
        IRI_SCHEME("iri-scheme", 89, 374, 6),
        JSON_ARRAY("json-array", 95, 421, 10),
        JSON_ESCAPE("json-escape", 97, 441, 15),
        JSON_NUMBER("json-number", 98, 456, 17),
        JSON_OBJECT("json-object", 94, 407, 14),
        JSON_STRING("json-string", 96, 431, 10),
        JSON_VALUE("json-value", 93, 399, 8),
        LEGAL_COLLECTION("legal-collection", 44, 209, 4),
        LEGAL_SINGLETON("legal-singleton", 34, 169, 4),
        LITERAL_ADDRESS("literal-address", 17, 91, 4),
        LITERAL_ARC_ADDRESS("literal-arc-address", 20, 107, 1),
        LITERAL_STATEMENT("literal-statement", 4, 26, 6),
        LOWER("LOWER", 81, 355, 1),
        MEMBER("member", 49, 226, 3),
        NAME_SCHEME_XREF("name-scheme-xref", 70, 283, 4),
        NOLEADING("noleading", 99, 473, 4),
        NOTRAILING("notrailing", 100, 477, 4),
        NZDIG("NZDIG", 85, 361, 1),
        ORDERED_MEMBER("ordered-member", 50, 229, 8),
        PEER_ROOT("peer-root", 21, 108, 4),
        PEER_ROOT_CONTEXT("peer-root-context", 5, 32, 6),
        PERSON_COLLECTION("person-collection", 43, 205, 4),
        PERSON_SINGLETON("person-singleton", 33, 165, 4),
        QUOTE("QUOTE", 86, 362, 1),
        RELATIONAL_STATEMENT("relational-statement", 3, 20, 6),
        RESERVED_CLASS("reserved-class", 37, 181, 4),
        RESERVED_COLLECTION("reserved-collection", 46, 217, 4),
        ROOT_ADDRESS("root-address", 13, 79, 5),
        ROOTED_ADDRESS("rooted-address", 12, 76, 3),
        STMT("STMT", 68, 281, 1),
        UNORDERED_MEMBER("unordered-member", 51, 237, 3),
        UNRESERVED_CLASS("unreserved-class", 36, 177, 4),
        UNRESERVED_COLLECTION("unreserved-collection", 47, 221, 4),
        UPPER("UPPER", 82, 356, 1),
        VARL("VARL", 66, 279, 1),
        VARR("VARR", 67, 280, 1),
        XDI_ADDRESS("xdi-address", 11, 73, 3),
        XDI_IPV6("xdi-ipv6", 77, 328, 9),
        XDI_IRI("xdi-iri", 88, 369, 5),
        XDI_NAME("xdi-name", 78, 337, 2),
        XDI_NAME_CHAR("xdi-name-char", 79, 339, 9),
        XDI_SCHEME("xdi-scheme", 72, 294, 6),
        XDI_SHA_256("xdi-sha-256", 74, 316, 4),
        XDI_SHA_384("xdi-sha-384", 75, 320, 4),
        XDI_SHA_512("xdi-sha-512", 76, 324, 4),
        XDI_STATEMENT("xdi-statement", 1, 8, 4),
        XDI_STATEMENTS("xdi-statements", 0, 0, 8),
        XDI_UUID("xdi-uuid", 73, 300, 16),
        XREF("xref", 71, 287, 7);
        private String name;
        private int id;
        private int offset;
        private int count;
        RuleNames(String string, int id, int offset, int count){
            this.name = string;
            this.id = id;
            this.offset = offset;
            this.count = count;
        }
        public  String ruleName(){return name;}
        public  int    ruleID(){return id;}
        private int    opcodeOffset(){return offset;}
        private int    opcodeCount(){return count;}
    }

    // UDT name enum
    public static int udtCount = 0;
    public enum UdtNames{
    }

    // private
    private static APGGrammar factoryInstance = null;
    private APGGrammar(Rule[] rules, Udt[] udts, Opcode[] opcodes){
        super(rules, udts, opcodes);
    }

    private static Rule[] getRules(){
    	Rule[] rules = new Rule[101];
        for(RuleNames r : RuleNames.values()){
            rules[r.ruleID()] = getRule(r.ruleID(), r.ruleName(), r.opcodeOffset(), r.opcodeCount());
        }
        return rules;
    }

    private static Udt[] getUdts(){
    	Udt[] udts = new Udt[0];
        return udts;
    }

        // opcodes
    private static Opcode[] getOpcodes(){
    	Opcode[] op = new Opcode[481];
        {int[] a = {1,2,6}; op[0] = getOpcodeCat(a);}
        op[1] = getOpcodeRnm(1, 8); // xdi-statement
        op[2] = getOpcodeRep((char)0, Character.MAX_VALUE, 3);
        {int[] a = {4,5}; op[3] = getOpcodeCat(a);}
        op[4] = getOpcodeRnm(87, 363); // ENDLINE
        op[5] = getOpcodeRnm(1, 8); // xdi-statement
        op[6] = getOpcodeRep((char)0, Character.MAX_VALUE, 7);
        op[7] = getOpcodeRnm(87, 363); // ENDLINE
        {int[] a = {9,10,11}; op[8] = getOpcodeAlt(a);}
        op[9] = getOpcodeRnm(2, 12); // contextual-statement
        op[10] = getOpcodeRnm(3, 20); // relational-statement
        op[11] = getOpcodeRnm(4, 26); // literal-statement
        {int[] a = {13,14,15,16,19}; op[12] = getOpcodeAlt(a);}
        op[13] = getOpcodeRnm(5, 32); // peer-root-context
        op[14] = getOpcodeRnm(6, 38); // inner-root-context
        op[15] = getOpcodeRnm(7, 43); // entity-part-context
        {int[] a = {17,18}; op[16] = getOpcodeCat(a);}
        op[17] = getOpcodeRnm(8, 52); // entity-member-context
        op[18] = getOpcodeRnm(9, 60); // attr-part-context
        op[19] = getOpcodeRnm(10, 67); // attr-member-context
        {int[] a = {21,22,23,24,25}; op[20] = getOpcodeCat(a);}
        op[21] = getOpcodeRnm(12, 76); // rooted-address
        op[22] = getOpcodeRnm(68, 281); // STMT
        op[23] = getOpcodeRnm(14, 84); // common-address
        op[24] = getOpcodeRnm(68, 281); // STMT
        op[25] = getOpcodeRnm(11, 73); // xdi-address
        {int[] a = {27,28,29,30,31}; op[26] = getOpcodeCat(a);}
        op[27] = getOpcodeRnm(12, 76); // rooted-address
        op[28] = getOpcodeRnm(68, 281); // STMT
        op[29] = getOpcodeRnm(20, 107); // literal-arc-address
        op[30] = getOpcodeRnm(68, 281); // STMT
        op[31] = getOpcodeRnm(93, 399); // json-value
        {int[] a = {33,35,36,37}; op[32] = getOpcodeCat(a);}
        op[33] = getOpcodeRep((char)0, Character.MAX_VALUE, 34);
        op[34] = getOpcodeRnm(21, 108); // peer-root
        op[35] = getOpcodeRnm(68, 281); // STMT
        op[36] = getOpcodeRnm(68, 281); // STMT
        op[37] = getOpcodeRnm(21, 108); // peer-root
        {int[] a = {39,40,41,42}; op[38] = getOpcodeCat(a);}
        op[39] = getOpcodeRnm(13, 79); // root-address
        op[40] = getOpcodeRnm(68, 281); // STMT
        op[41] = getOpcodeRnm(68, 281); // STMT
        op[42] = getOpcodeRnm(22, 112); // inner-root
        {int[] a = {44,45,47,48,49}; op[43] = getOpcodeCat(a);}
        op[44] = getOpcodeRnm(13, 79); // root-address
        op[45] = getOpcodeRep((char)0, Character.MAX_VALUE, 46);
        op[46] = getOpcodeRnm(24, 123); // entity-part
        op[47] = getOpcodeRnm(68, 281); // STMT
        op[48] = getOpcodeRnm(68, 281); // STMT
        {int[] a = {50,51}; op[49] = getOpcodeAlt(a);}
        op[50] = getOpcodeRnm(27, 140); // entity-singleton
        op[51] = getOpcodeRnm(38, 185); // entity-collection
        {int[] a = {53,54,56,57,58,59}; op[52] = getOpcodeCat(a);}
        op[53] = getOpcodeRnm(13, 79); // root-address
        op[54] = getOpcodeRep((char)0, Character.MAX_VALUE, 55);
        op[55] = getOpcodeRnm(24, 123); // entity-part
        op[56] = getOpcodeRnm(38, 185); // entity-collection
        op[57] = getOpcodeRnm(68, 281); // STMT
        op[58] = getOpcodeRnm(68, 281); // STMT
        op[59] = getOpcodeRnm(48, 225); // entity-member
        {int[] a = {61,62,63,64}; op[60] = getOpcodeCat(a);}
        op[61] = getOpcodeRnm(12, 76); // rooted-address
        op[62] = getOpcodeRnm(68, 281); // STMT
        op[63] = getOpcodeRnm(68, 281); // STMT
        {int[] a = {65,66}; op[64] = getOpcodeAlt(a);}
        op[65] = getOpcodeRnm(52, 240); // attr-singleton
        op[66] = getOpcodeRnm(53, 244); // attr-collection
        {int[] a = {68,69,70,71,72}; op[67] = getOpcodeCat(a);}
        op[68] = getOpcodeRnm(12, 76); // rooted-address
        op[69] = getOpcodeRnm(53, 244); // attr-collection
        op[70] = getOpcodeRnm(68, 281); // STMT
        op[71] = getOpcodeRnm(68, 281); // STMT
        op[72] = getOpcodeRnm(59, 268); // attr-member
        {int[] a = {74,75}; op[73] = getOpcodeAlt(a);}
        op[74] = getOpcodeRnm(17, 91); // literal-address
        op[75] = getOpcodeRnm(12, 76); // rooted-address
        {int[] a = {77,78}; op[76] = getOpcodeCat(a);}
        op[77] = getOpcodeRnm(13, 79); // root-address
        op[78] = getOpcodeRnm(14, 84); // common-address
        {int[] a = {80,82}; op[79] = getOpcodeCat(a);}
        op[80] = getOpcodeRep((char)0, Character.MAX_VALUE, 81);
        op[81] = getOpcodeRnm(21, 108); // peer-root
        op[82] = getOpcodeRep((char)0, Character.MAX_VALUE, 83);
        op[83] = getOpcodeRnm(22, 112); // inner-root
        {int[] a = {85,86}; op[84] = getOpcodeCat(a);}
        op[85] = getOpcodeRnm(15, 87); // entity-address
        op[86] = getOpcodeRnm(16, 89); // attr-address
        op[87] = getOpcodeRep((char)0, Character.MAX_VALUE, 88);
        op[88] = getOpcodeRnm(24, 123); // entity-part
        op[89] = getOpcodeRep((char)0, Character.MAX_VALUE, 90);
        op[90] = getOpcodeRnm(26, 134); // attr-part
        {int[] a = {92,93,94}; op[91] = getOpcodeCat(a);}
        op[92] = getOpcodeRnm(12, 76); // rooted-address
        op[93] = getOpcodeRnm(25, 129); // attr-unit
        {char[] a = {38}; op[94] = getOpcodeTls(a);}
        {int[] a = {96,97}; op[95] = getOpcodeAlt(a);}
        op[96] = getOpcodeRnm(19, 98); // common-arc-address
        op[97] = getOpcodeRnm(20, 107); // literal-arc-address
        {int[] a = {99,100,101,102,103,104,105,106}; op[98] = getOpcodeAlt(a);}
        op[99] = getOpcodeRnm(21, 108); // peer-root
        op[100] = getOpcodeRnm(22, 112); // inner-root
        op[101] = getOpcodeRnm(27, 140); // entity-singleton
        op[102] = getOpcodeRnm(38, 185); // entity-collection
        op[103] = getOpcodeRnm(48, 225); // entity-member
        op[104] = getOpcodeRnm(52, 240); // attr-singleton
        op[105] = getOpcodeRnm(53, 244); // attr-collection
        op[106] = getOpcodeRnm(59, 268); // attr-member
        {char[] a = {38}; op[107] = getOpcodeTls(a);}
        {int[] a = {109,110,111}; op[108] = getOpcodeCat(a);}
        {char[] a = {40}; op[109] = getOpcodeTls(a);}
        op[110] = getOpcodeRnm(14, 84); // common-address
        {char[] a = {41}; op[111] = getOpcodeTls(a);}
        {int[] a = {113,114,115,116,117}; op[112] = getOpcodeCat(a);}
        {char[] a = {40}; op[113] = getOpcodeTls(a);}
        op[114] = getOpcodeRnm(14, 84); // common-address
        {char[] a = {47}; op[115] = getOpcodeTls(a);}
        op[116] = getOpcodeRnm(14, 84); // common-address
        {char[] a = {41}; op[117] = getOpcodeTls(a);}
        {int[] a = {119,122}; op[118] = getOpcodeAlt(a);}
        {int[] a = {120,121}; op[119] = getOpcodeCat(a);}
        op[120] = getOpcodeRnm(38, 185); // entity-collection
        op[121] = getOpcodeRnm(48, 225); // entity-member
        op[122] = getOpcodeRnm(27, 140); // entity-singleton
        {int[] a = {124,128}; op[123] = getOpcodeAlt(a);}
        {int[] a = {125,126}; op[124] = getOpcodeCat(a);}
        op[125] = getOpcodeRnm(38, 185); // entity-collection
        op[126] = getOpcodeRep((char)0, (char)1, 127);
        op[127] = getOpcodeRnm(48, 225); // entity-member
        op[128] = getOpcodeRnm(27, 140); // entity-singleton
        {int[] a = {130,133}; op[129] = getOpcodeAlt(a);}
        {int[] a = {131,132}; op[130] = getOpcodeCat(a);}
        op[131] = getOpcodeRnm(53, 244); // attr-collection
        op[132] = getOpcodeRnm(59, 268); // attr-member
        op[133] = getOpcodeRnm(52, 240); // attr-singleton
        {int[] a = {135,139}; op[134] = getOpcodeAlt(a);}
        {int[] a = {136,137}; op[135] = getOpcodeCat(a);}
        op[136] = getOpcodeRnm(53, 244); // attr-collection
        op[137] = getOpcodeRep((char)0, (char)1, 138);
        op[138] = getOpcodeRnm(59, 268); // attr-member
        op[139] = getOpcodeRnm(52, 240); // attr-singleton
        {int[] a = {141,142,143,144,145}; op[140] = getOpcodeAlt(a);}
        op[141] = getOpcodeRnm(31, 158); // authority-singleton
        op[142] = getOpcodeRnm(28, 146); // authority-variable
        op[143] = getOpcodeRnm(32, 162); // class-singleton
        op[144] = getOpcodeRnm(29, 150); // class-variable
        op[145] = getOpcodeRnm(30, 154); // class-definition
        {int[] a = {147,148,149}; op[146] = getOpcodeCat(a);}
        op[147] = getOpcodeRnm(66, 279); // VARL
        op[148] = getOpcodeRnm(31, 158); // authority-singleton
        op[149] = getOpcodeRnm(67, 280); // VARR
        {int[] a = {151,152,153}; op[150] = getOpcodeCat(a);}
        op[151] = getOpcodeRnm(66, 279); // VARL
        op[152] = getOpcodeRnm(32, 162); // class-singleton
        op[153] = getOpcodeRnm(67, 280); // VARR
        {int[] a = {155,156,157}; op[154] = getOpcodeCat(a);}
        op[155] = getOpcodeRnm(64, 277); // DEFL
        op[156] = getOpcodeRnm(32, 162); // class-singleton
        op[157] = getOpcodeRnm(64, 277); // DEFL
        {int[] a = {159,160,161}; op[158] = getOpcodeAlt(a);}
        op[159] = getOpcodeRnm(33, 165); // person-singleton
        op[160] = getOpcodeRnm(34, 169); // legal-singleton
        op[161] = getOpcodeRnm(35, 173); // general-singleton
        {int[] a = {163,164}; op[162] = getOpcodeAlt(a);}
        op[163] = getOpcodeRnm(36, 177); // unreserved-class
        op[164] = getOpcodeRnm(37, 181); // reserved-class
        {int[] a = {166,167,168}; op[165] = getOpcodeCat(a);}
        {char[] a = {61}; op[166] = getOpcodeTls(a);}
        op[167] = getOpcodeRnm(69, 282); // C-I
        op[168] = getOpcodeRnm(70, 283); // name-scheme-xref
        {int[] a = {170,171,172}; op[169] = getOpcodeCat(a);}
        {char[] a = {43}; op[170] = getOpcodeTls(a);}
        op[171] = getOpcodeRnm(69, 282); // C-I
        op[172] = getOpcodeRnm(70, 283); // name-scheme-xref
        {int[] a = {174,175,176}; op[173] = getOpcodeCat(a);}
        {char[] a = {42}; op[174] = getOpcodeTls(a);}
        op[175] = getOpcodeRnm(69, 282); // C-I
        op[176] = getOpcodeRnm(70, 283); // name-scheme-xref
        {int[] a = {178,179,180}; op[177] = getOpcodeCat(a);}
        {char[] a = {35}; op[178] = getOpcodeTls(a);}
        op[179] = getOpcodeRnm(69, 282); // C-I
        op[180] = getOpcodeRnm(70, 283); // name-scheme-xref
        {int[] a = {182,183,184}; op[181] = getOpcodeCat(a);}
        {char[] a = {36}; op[182] = getOpcodeTls(a);}
        op[183] = getOpcodeRnm(69, 282); // C-I
        op[184] = getOpcodeRnm(78, 337); // xdi-name
        {int[] a = {186,187,188,189}; op[185] = getOpcodeAlt(a);}
        op[186] = getOpcodeRnm(41, 198); // authority-collection
        op[187] = getOpcodeRnm(39, 190); // auth-var-coll
        op[188] = getOpcodeRnm(42, 202); // class-collection
        op[189] = getOpcodeRnm(40, 194); // class-var-coll
        {int[] a = {191,192,193}; op[190] = getOpcodeCat(a);}
        op[191] = getOpcodeRnm(66, 279); // VARL
        op[192] = getOpcodeRnm(41, 198); // authority-collection
        op[193] = getOpcodeRnm(67, 280); // VARR
        {int[] a = {195,196,197}; op[194] = getOpcodeCat(a);}
        op[195] = getOpcodeRnm(66, 279); // VARL
        op[196] = getOpcodeRnm(42, 202); // class-collection
        op[197] = getOpcodeRnm(67, 280); // VARR
        {int[] a = {199,200,201}; op[198] = getOpcodeAlt(a);}
        op[199] = getOpcodeRnm(43, 205); // person-collection
        op[200] = getOpcodeRnm(44, 209); // legal-collection
        op[201] = getOpcodeRnm(45, 213); // general-collection
        {int[] a = {203,204}; op[202] = getOpcodeAlt(a);}
        op[203] = getOpcodeRnm(46, 217); // reserved-collection
        op[204] = getOpcodeRnm(47, 221); // unreserved-collection
        {int[] a = {206,207,208}; op[205] = getOpcodeCat(a);}
        op[206] = getOpcodeRnm(60, 273); // COLL
        {char[] a = {61}; op[207] = getOpcodeTls(a);}
        op[208] = getOpcodeRnm(61, 274); // COLR
        {int[] a = {210,211,212}; op[209] = getOpcodeCat(a);}
        op[210] = getOpcodeRnm(60, 273); // COLL
        {char[] a = {43}; op[211] = getOpcodeTls(a);}
        op[212] = getOpcodeRnm(61, 274); // COLR
        {int[] a = {214,215,216}; op[213] = getOpcodeCat(a);}
        op[214] = getOpcodeRnm(60, 273); // COLL
        {char[] a = {42}; op[215] = getOpcodeTls(a);}
        op[216] = getOpcodeRnm(61, 274); // COLR
        {int[] a = {218,219,220}; op[217] = getOpcodeCat(a);}
        op[218] = getOpcodeRnm(60, 273); // COLL
        op[219] = getOpcodeRnm(37, 181); // reserved-class
        op[220] = getOpcodeRnm(61, 274); // COLR
        {int[] a = {222,223,224}; op[221] = getOpcodeCat(a);}
        op[222] = getOpcodeRnm(60, 273); // COLL
        op[223] = getOpcodeRnm(36, 177); // unreserved-class
        op[224] = getOpcodeRnm(61, 274); // COLR
        op[225] = getOpcodeRnm(49, 226); // member
        {int[] a = {227,228}; op[226] = getOpcodeAlt(a);}
        op[227] = getOpcodeRnm(50, 229); // ordered-member
        op[228] = getOpcodeRnm(51, 237); // unordered-member
        {int[] a = {230,231,232}; op[229] = getOpcodeCat(a);}
        {char[] a = {64}; op[230] = getOpcodeTls(a);}
        op[231] = getOpcodeRnm(69, 282); // C-I
        {int[] a = {233,236}; op[232] = getOpcodeAlt(a);}
        {int[] a = {234,235}; op[233] = getOpcodeCat(a);}
        op[234] = getOpcodeTrg((char)49, (char)57);
        op[235] = getOpcodeRnm(84, 360); // DIGIT
        {char[] a = {48}; op[236] = getOpcodeTls(a);}
        {int[] a = {238,239}; op[237] = getOpcodeCat(a);}
        {char[] a = {33}; op[238] = getOpcodeTls(a);}
        op[239] = getOpcodeRnm(70, 283); // name-scheme-xref
        {int[] a = {241,242,243}; op[240] = getOpcodeAlt(a);}
        op[241] = getOpcodeRnm(58, 263); // attr-class
        op[242] = getOpcodeRnm(55, 251); // attr-var
        op[243] = getOpcodeRnm(57, 259); // attr-defn
        {int[] a = {245,246}; op[244] = getOpcodeAlt(a);}
        op[245] = getOpcodeRnm(56, 255); // attr-coll-nonvar
        op[246] = getOpcodeRnm(54, 247); // attr-coll-var
        {int[] a = {248,249,250}; op[247] = getOpcodeCat(a);}
        op[248] = getOpcodeRnm(66, 279); // VARL
        op[249] = getOpcodeRnm(56, 255); // attr-coll-nonvar
        op[250] = getOpcodeRnm(67, 280); // VARR
        {int[] a = {252,253,254}; op[251] = getOpcodeCat(a);}
        op[252] = getOpcodeRnm(66, 279); // VARL
        op[253] = getOpcodeRnm(58, 263); // attr-class
        op[254] = getOpcodeRnm(67, 280); // VARR
        {int[] a = {256,257,258}; op[255] = getOpcodeCat(a);}
        op[256] = getOpcodeRnm(60, 273); // COLL
        op[257] = getOpcodeRnm(58, 263); // attr-class
        op[258] = getOpcodeRnm(61, 274); // COLR
        {int[] a = {260,261,262}; op[259] = getOpcodeCat(a);}
        op[260] = getOpcodeRnm(64, 277); // DEFL
        op[261] = getOpcodeRnm(58, 263); // attr-class
        op[262] = getOpcodeRnm(65, 278); // DEFR
        {int[] a = {264,265,266,267}; op[263] = getOpcodeCat(a);}
        op[264] = getOpcodeRnm(62, 275); // ATTL
        op[265] = getOpcodeRnm(69, 282); // C-I
        op[266] = getOpcodeRnm(32, 162); // class-singleton
        op[267] = getOpcodeRnm(63, 276); // ATTR
        {int[] a = {269,270,271,272}; op[268] = getOpcodeCat(a);}
        op[269] = getOpcodeRnm(62, 275); // ATTL
        op[270] = getOpcodeRnm(69, 282); // C-I
        op[271] = getOpcodeRnm(49, 226); // member
        op[272] = getOpcodeRnm(63, 276); // ATTR
        {char[] a = {91}; op[273] = getOpcodeTls(a);}
        {char[] a = {93}; op[274] = getOpcodeTls(a);}
        {char[] a = {60}; op[275] = getOpcodeTls(a);}
        {char[] a = {62}; op[276] = getOpcodeTls(a);}
        {char[] a = {124}; op[277] = getOpcodeTls(a);}
        {char[] a = {124}; op[278] = getOpcodeTls(a);}
        {char[] a = {123}; op[279] = getOpcodeTls(a);}
        {char[] a = {125}; op[280] = getOpcodeTls(a);}
        {char[] a = {47}; op[281] = getOpcodeTls(a);}
        {char[] a = {}; op[282] = getOpcodeTls(a);}
        {int[] a = {284,285,286}; op[283] = getOpcodeAlt(a);}
        op[284] = getOpcodeRnm(78, 337); // xdi-name
        op[285] = getOpcodeRnm(72, 294); // xdi-scheme
        op[286] = getOpcodeRnm(71, 287); // xref
        {int[] a = {288,289,293}; op[287] = getOpcodeCat(a);}
        {char[] a = {40}; op[288] = getOpcodeTls(a);}
        {int[] a = {290,291,292}; op[289] = getOpcodeAlt(a);}
        op[290] = getOpcodeRnm(14, 84); // common-address
        op[291] = getOpcodeRnm(88, 369); // xdi-iri
        op[292] = getOpcodeRnm(78, 337); // xdi-name
        {char[] a = {41}; op[293] = getOpcodeTls(a);}
        {int[] a = {295,296,297,298,299}; op[294] = getOpcodeAlt(a);}
        op[295] = getOpcodeRnm(74, 316); // xdi-sha-256
        op[296] = getOpcodeRnm(75, 320); // xdi-sha-384
        op[297] = getOpcodeRnm(76, 324); // xdi-sha-512
        op[298] = getOpcodeRnm(73, 300); // xdi-uuid
        op[299] = getOpcodeRnm(77, 328); // xdi-ipv6
        {int[] a = {301,302,304,305,307,308,310,311,313,314}; op[300] = getOpcodeCat(a);}
        {char[] a = {58,117,117,105,100,58}; op[301] = getOpcodeTls(a);}
        op[302] = getOpcodeRep((char)8, (char)8, 303);
        op[303] = getOpcodeRnm(83, 357); // HEX
        {char[] a = {45}; op[304] = getOpcodeTls(a);}
        op[305] = getOpcodeRep((char)4, (char)4, 306);
        op[306] = getOpcodeRnm(83, 357); // HEX
        {char[] a = {45}; op[307] = getOpcodeTls(a);}
        op[308] = getOpcodeRep((char)4, (char)4, 309);
        op[309] = getOpcodeRnm(83, 357); // HEX
        {char[] a = {45}; op[310] = getOpcodeTls(a);}
        op[311] = getOpcodeRep((char)4, (char)4, 312);
        op[312] = getOpcodeRnm(83, 357); // HEX
        {char[] a = {45}; op[313] = getOpcodeTls(a);}
        op[314] = getOpcodeRep((char)12, (char)12, 315);
        op[315] = getOpcodeRnm(83, 357); // HEX
        {int[] a = {317,318}; op[316] = getOpcodeCat(a);}
        {char[] a = {58,115,104,97,45,50,53,54,58}; op[317] = getOpcodeTls(a);}
        op[318] = getOpcodeRep((char)64, (char)64, 319);
        op[319] = getOpcodeRnm(83, 357); // HEX
        {int[] a = {321,322}; op[320] = getOpcodeCat(a);}
        {char[] a = {58,115,104,97,45,51,56,52,58}; op[321] = getOpcodeTls(a);}
        op[322] = getOpcodeRep((char)96, (char)96, 323);
        op[323] = getOpcodeRnm(83, 357); // HEX
        {int[] a = {325,326}; op[324] = getOpcodeCat(a);}
        {char[] a = {58,115,104,97,45,53,49,50,58}; op[325] = getOpcodeTls(a);}
        op[326] = getOpcodeRep((char)128, (char)128, 327);
        op[327] = getOpcodeRnm(83, 357); // HEX
        {int[] a = {329,330,332}; op[328] = getOpcodeCat(a);}
        {char[] a = {58,105,112,118,54,58}; op[329] = getOpcodeTls(a);}
        op[330] = getOpcodeRep((char)4, (char)4, 331);
        op[331] = getOpcodeRnm(83, 357); // HEX
        op[332] = getOpcodeRep((char)7, Character.MAX_VALUE, 333);
        {int[] a = {334,335}; op[333] = getOpcodeCat(a);}
        {char[] a = {58}; op[334] = getOpcodeTls(a);}
        op[335] = getOpcodeRep((char)4, (char)4, 336);
        op[336] = getOpcodeRnm(83, 357); // HEX
        op[337] = getOpcodeRep((char)0, Character.MAX_VALUE, 338);
        op[338] = getOpcodeRnm(79, 339); // xdi-name-char
        {int[] a = {340,341,342,343,344,345,346,347}; op[339] = getOpcodeAlt(a);}
        op[340] = getOpcodeRnm(82, 356); // UPPER
        op[341] = getOpcodeRnm(81, 355); // LOWER
        op[342] = getOpcodeRnm(84, 360); // DIGIT
        {char[] a = {45}; op[343] = getOpcodeTls(a);}
        {char[] a = {46}; op[344] = getOpcodeTls(a);}
        {char[] a = {95}; op[345] = getOpcodeTls(a);}
        {char[] a = {58}; op[346] = getOpcodeTls(a);}
        op[347] = getOpcodeTrg((char)128, (char)65533);
        {int[] a = {349,350,351,352,353,354}; op[348] = getOpcodeAlt(a);}
        {char[] a = {33}; op[349] = getOpcodeTls(a);}
        {char[] a = {42}; op[350] = getOpcodeTls(a);}
        {char[] a = {61}; op[351] = getOpcodeTls(a);}
        {char[] a = {35}; op[352] = getOpcodeTls(a);}
        {char[] a = {43}; op[353] = getOpcodeTls(a);}
        {char[] a = {36}; op[354] = getOpcodeTls(a);}
        op[355] = getOpcodeTrg((char)97, (char)122);
        op[356] = getOpcodeTrg((char)65, (char)90);
        {int[] a = {358,359}; op[357] = getOpcodeAlt(a);}
        op[358] = getOpcodeRnm(84, 360); // DIGIT
        op[359] = getOpcodeTrg((char)97, (char)102);
        op[360] = getOpcodeTrg((char)48, (char)57);
        op[361] = getOpcodeTrg((char)49, (char)57);
        {char[] a = {34}; op[362] = getOpcodeTbs(a);}
        {int[] a = {364,365,366}; op[363] = getOpcodeAlt(a);}
        {char[] a = {13}; op[364] = getOpcodeTbs(a);}
        {char[] a = {10}; op[365] = getOpcodeTbs(a);}
        {int[] a = {367,368}; op[366] = getOpcodeCat(a);}
        {char[] a = {13}; op[367] = getOpcodeTbs(a);}
        {char[] a = {10}; op[368] = getOpcodeTbs(a);}
        {int[] a = {370,371,372}; op[369] = getOpcodeCat(a);}
        op[370] = getOpcodeRnm(89, 374); // iri-scheme
        {char[] a = {58}; op[371] = getOpcodeTls(a);}
        op[372] = getOpcodeRep((char)0, Character.MAX_VALUE, 373);
        op[373] = getOpcodeRnm(90, 380); // iri-body
        {int[] a = {375,376}; op[374] = getOpcodeCat(a);}
        op[375] = getOpcodeRnm(81, 355); // LOWER
        op[376] = getOpcodeRep((char)0, Character.MAX_VALUE, 377);
        {int[] a = {378,379}; op[377] = getOpcodeAlt(a);}
        op[378] = getOpcodeRnm(81, 355); // LOWER
        op[379] = getOpcodeRnm(84, 360); // DIGIT
        {int[] a = {381,385}; op[380] = getOpcodeAlt(a);}
        {int[] a = {382,383,384}; op[381] = getOpcodeCat(a);}
        {char[] a = {40}; op[382] = getOpcodeTls(a);}
        op[383] = getOpcodeRnm(90, 380); // iri-body
        {char[] a = {41}; op[384] = getOpcodeTls(a);}
        op[385] = getOpcodeRep((char)0, Character.MAX_VALUE, 386);
        op[386] = getOpcodeRnm(91, 387); // iri-char
        {int[] a = {388,389,390}; op[387] = getOpcodeAlt(a);}
        op[388] = getOpcodeRnm(79, 339); // xdi-name-char
        op[389] = getOpcodeRnm(80, 348); // context-symbol
        op[390] = getOpcodeRnm(92, 391); // iri-delim
        {int[] a = {392,393,394,395,396,397,398}; op[391] = getOpcodeAlt(a);}
        {char[] a = {47}; op[392] = getOpcodeTls(a);}
        {char[] a = {63}; op[393] = getOpcodeTls(a);}
        {char[] a = {35}; op[394] = getOpcodeTls(a);}
        {char[] a = {91}; op[395] = getOpcodeTls(a);}
        {char[] a = {93}; op[396] = getOpcodeTls(a);}
        {char[] a = {39}; op[397] = getOpcodeTls(a);}
        {char[] a = {44}; op[398] = getOpcodeTls(a);}
        {int[] a = {400,401,402,403,404,405,406}; op[399] = getOpcodeAlt(a);}
        op[400] = getOpcodeRnm(94, 407); // json-object
        op[401] = getOpcodeRnm(95, 421); // json-array
        op[402] = getOpcodeRnm(96, 431); // json-string
        op[403] = getOpcodeRnm(98, 456); // json-number
        {char[] a = {116,114,117,101}; op[404] = getOpcodeTls(a);}
        {char[] a = {102,97,108,115,101}; op[405] = getOpcodeTls(a);}
        {char[] a = {110,117,108,108}; op[406] = getOpcodeTls(a);}
        {int[] a = {408,409,420}; op[407] = getOpcodeCat(a);}
        {char[] a = {123}; op[408] = getOpcodeTls(a);}
        op[409] = getOpcodeRep((char)0, (char)1, 410);
        {int[] a = {411,412,413,414}; op[410] = getOpcodeCat(a);}
        op[411] = getOpcodeRnm(96, 431); // json-string
        {char[] a = {58}; op[412] = getOpcodeTls(a);}
        op[413] = getOpcodeRnm(93, 399); // json-value
        op[414] = getOpcodeRep((char)0, Character.MAX_VALUE, 415);
        {int[] a = {416,417,418,419}; op[415] = getOpcodeCat(a);}
        {char[] a = {44}; op[416] = getOpcodeTls(a);}
        op[417] = getOpcodeRnm(96, 431); // json-string
        {char[] a = {58}; op[418] = getOpcodeTls(a);}
        op[419] = getOpcodeRnm(93, 399); // json-value
        {char[] a = {125}; op[420] = getOpcodeTls(a);}
        {int[] a = {422,423,430}; op[421] = getOpcodeCat(a);}
        {char[] a = {91}; op[422] = getOpcodeTls(a);}
        op[423] = getOpcodeRep((char)0, (char)1, 424);
        {int[] a = {425,426}; op[424] = getOpcodeCat(a);}
        op[425] = getOpcodeRnm(93, 399); // json-value
        op[426] = getOpcodeRep((char)0, Character.MAX_VALUE, 427);
        {int[] a = {428,429}; op[427] = getOpcodeCat(a);}
        {char[] a = {44}; op[428] = getOpcodeTls(a);}
        op[429] = getOpcodeRnm(93, 399); // json-value
        {char[] a = {93}; op[430] = getOpcodeTls(a);}
        {int[] a = {432,433,440}; op[431] = getOpcodeCat(a);}
        op[432] = getOpcodeRnm(86, 362); // QUOTE
        op[433] = getOpcodeRep((char)0, Character.MAX_VALUE, 434);
        {int[] a = {435,436,437,438,439}; op[434] = getOpcodeAlt(a);}
        {char[] a = {32}; op[435] = getOpcodeTls(a);}
        {char[] a = {33}; op[436] = getOpcodeTls(a);}
        op[437] = getOpcodeTrg((char)35, (char)91);
        op[438] = getOpcodeTrg((char)93, (char)65533);
        op[439] = getOpcodeRnm(97, 441); // json-escape
        op[440] = getOpcodeRnm(86, 362); // QUOTE
        {int[] a = {442,443}; op[441] = getOpcodeCat(a);}
        {char[] a = {92}; op[442] = getOpcodeTls(a);}
        {int[] a = {444,445,446,447,448,449,450,451,452}; op[443] = getOpcodeAlt(a);}
        {char[] a = {92}; op[444] = getOpcodeTls(a);}
        {char[] a = {47}; op[445] = getOpcodeTls(a);}
        {char[] a = {98}; op[446] = getOpcodeTls(a);}
        {char[] a = {102}; op[447] = getOpcodeTls(a);}
        {char[] a = {110}; op[448] = getOpcodeTls(a);}
        {char[] a = {114}; op[449] = getOpcodeTls(a);}
        {char[] a = {116}; op[450] = getOpcodeTls(a);}
        op[451] = getOpcodeRnm(86, 362); // QUOTE
        {int[] a = {453,454}; op[452] = getOpcodeCat(a);}
        {char[] a = {117}; op[453] = getOpcodeTls(a);}
        op[454] = getOpcodeRep((char)4, (char)4, 455);
        op[455] = getOpcodeRnm(83, 357); // HEX
        {int[] a = {457,463,467}; op[456] = getOpcodeCat(a);}
        {int[] a = {458,459}; op[457] = getOpcodeAlt(a);}
        {char[] a = {48}; op[458] = getOpcodeTls(a);}
        {int[] a = {460,462}; op[459] = getOpcodeCat(a);}
        op[460] = getOpcodeRep((char)0, (char)1, 461);
        {char[] a = {45}; op[461] = getOpcodeTls(a);}
        op[462] = getOpcodeRnm(99, 473); // noleading
        op[463] = getOpcodeRep((char)0, (char)1, 464);
        {int[] a = {465,466}; op[464] = getOpcodeCat(a);}
        {char[] a = {46}; op[465] = getOpcodeTls(a);}
        op[466] = getOpcodeRnm(100, 477); // notrailing
        op[467] = getOpcodeRep((char)0, (char)1, 468);
        {int[] a = {469,470,472}; op[468] = getOpcodeCat(a);}
        {char[] a = {69}; op[469] = getOpcodeTls(a);}
        op[470] = getOpcodeRep((char)0, (char)1, 471);
        {char[] a = {45}; op[471] = getOpcodeTls(a);}
        op[472] = getOpcodeRnm(99, 473); // noleading
        {int[] a = {474,475}; op[473] = getOpcodeCat(a);}
        op[474] = getOpcodeTrg((char)49, (char)57);
        op[475] = getOpcodeRep((char)0, Character.MAX_VALUE, 476);
        op[476] = getOpcodeRnm(84, 360); // DIGIT
        {int[] a = {478,480}; op[477] = getOpcodeCat(a);}
        op[478] = getOpcodeRep((char)0, Character.MAX_VALUE, 479);
        op[479] = getOpcodeRnm(84, 360); // DIGIT
        op[480] = getOpcodeTrg((char)49, (char)57);
        return op;
    }

    public static void display(PrintStream out){
        out.println(";");
        out.println("; xdi2.core.syntax.apg.APGGrammar");
        out.println(";");
        out.println("");
        out.println("xdi-statements         = xdi-statement *( ENDLINE xdi-statement ) *ENDLINE");
        out.println("xdi-statement          = contextual-statement / relational-statement / literal-statement");
        out.println("contextual-statement   = peer-root-context / inner-root-context / entity-part-context / entity-member-context");
        out.println("                         attr-part-context / attr-member-context");
        out.println("relational-statement   = rooted-address STMT common-address STMT xdi-address");
        out.println("literal-statement      = rooted-address STMT literal-arc-address STMT json-value");
        out.println("");
        out.println("peer-root-context      = *peer-root STMT STMT peer-root");
        out.println("inner-root-context     = root-address STMT STMT inner-root");
        out.println("entity-part-context    = root-address *entity-part STMT STMT ( entity-singleton / entity-collection )");
        out.println("entity-member-context  = root-address *entity-part entity-collection STMT STMT entity-member");
        out.println("attr-part-context      = rooted-address STMT STMT ( attr-singleton / attr-collection )");
        out.println("attr-member-context    = rooted-address attr-collection STMT STMT attr-member");
        out.println("");
        out.println("xdi-address            = literal-address / rooted-address");
        out.println("rooted-address         = root-address common-address");
        out.println("root-address           = *peer-root *inner-root");
        out.println("common-address         = entity-address attr-address");
        out.println("entity-address         = *entity-part");
        out.println("attr-address           = *attr-part");
        out.println("literal-address        = rooted-address attr-unit \"&\"");
        out.println("");
        out.println("arc-address            = common-arc-address / literal-arc-address");
        out.println("");
        out.println("common-arc-address     = peer-root / inner-root / entity-singleton / entity-collection / entity-member /");
        out.println("                         attr-singleton / attr-collection / attr-member");
        out.println("");
        out.println("literal-arc-address    = \"&\"");
        out.println("");
        out.println("peer-root              = \"(\" common-address \")\"");
        out.println("inner-root             = \"(\" common-address \"/\" common-address \")\"");
        out.println("");
        out.println("entity-unit            = ( entity-collection entity-member ) / entity-singleton");
        out.println("entity-part            = ( entity-collection [ entity-member ] ) / entity-singleton");
        out.println("attr-unit              = ( attr-collection attr-member ) / attr-singleton");
        out.println("attr-part              = ( attr-collection [ attr-member ] ) / attr-singleton");
        out.println("");
        out.println("entity-singleton     = authority-singleton / authority-variable / class-singleton / class-variable / class-definition ");
        out.println("authority-variable   = VARL authority-singleton VARR");
        out.println("class-variable       = VARL class-singleton VARR");
        out.println("class-definition     = DEFL class-singleton DEFL");
        out.println("");
        out.println("authority-singleton  = person-singleton / legal-singleton / general-singleton");
        out.println("class-singleton      = unreserved-class / reserved-class");
        out.println("person-singleton     = \"=\" C-I name-scheme-xref");
        out.println("legal-singleton      = \"+\" C-I name-scheme-xref");
        out.println("general-singleton    = \"*\" C-I name-scheme-xref");
        out.println("unreserved-class     = \"#\" C-I name-scheme-xref");
        out.println("reserved-class       = \"$\" C-I xdi-name");
        out.println("");
        out.println("entity-collection    = authority-collection / auth-var-coll / class-collection / class-var-coll");
        out.println("auth-var-coll        = VARL authority-collection VARR");
        out.println("class-var-coll       = VARL class-collection     VARR");
        out.println("authority-collection = person-collection    / legal-collection / general-collection");
        out.println("class-collection     = reserved-collection  / unreserved-collection");
        out.println("person-collection    = COLL              \"=\" COLR ");
        out.println("legal-collection     = COLL              \"+\" COLR ");
        out.println("general-collection   = COLL              \"*\" COLR ");
        out.println("reserved-collection  = COLL   reserved-class COLR");
        out.println("unreserved-collection= COLL unreserved-class COLR");
        out.println("");
        out.println("entity-member        = member ");
        out.println("member               = ordered-member / unordered-member");
        out.println("ordered-member       = \"@\" C-I ( ( %x31-39 DIGIT ) / \"0\" )");
        out.println("unordered-member     = \"!\" name-scheme-xref");
        out.println("");
        out.println("attr-singleton       = attr-class/ attr-var / attr-defn ");
        out.println("attr-collection      = attr-coll-nonvar / attr-coll-var");
        out.println("attr-coll-var        = VARL attr-coll-nonvar VARR");
        out.println("attr-var             = VARL attr-class  VARR");
        out.println("attr-coll-nonvar     = COLL attr-class  COLR");
        out.println("attr-defn            = DEFL attr-class  DEFR");
        out.println("attr-class           = ATTL C-I class-singleton ATTR");
        out.println("attr-member          = ATTL C-I  member ATTR ");
        out.println("");
        out.println("");
        out.println("COLL     = \"[\" ");
        out.println("COLR     = \"]\"");
        out.println("ATTL     = \"<\" ");
        out.println("ATTR     = \">\" ");
        out.println("DEFL     = \"|\"");
        out.println("DEFR     = \"|\"");
        out.println("VARL     = \"{\" ");
        out.println("VARR     = \"}\"");
        out.println("STMT     = \"/\"");
        out.println("C-I    = \"\"");
        out.println("");
        out.println("name-scheme-xref     = xdi-name / xdi-scheme / xref");
        out.println("xref        = \"(\" ( common-address / xdi-iri / xdi-name ) \")\"");
        out.println("");
        out.println("xdi-scheme   =  xdi-sha-256 / xdi-sha-384 / xdi-sha-512 / xdi-uuid / xdi-ipv6");
        out.println("xdi-uuid     = \":uuid:\"      8HEX \"-\" 4HEX \"-\" 4HEX \"-\" 4HEX \"-\" 12HEX");
        out.println("xdi-sha-256  = \":sha-256:\"  64HEX ");
        out.println("xdi-sha-384  = \":sha-384:\"  96HEX ");
        out.println("xdi-sha-512  = \":sha-512:\" 128HEX ");
        out.println("xdi-ipv6     = \":ipv6:\"      4HEX 7*( \":\" 4HEX )  ");
        out.println("");
        out.println("xdi-name       = *xdi-name-char");
        out.println("xdi-name-char  = UPPER / LOWER / DIGIT / \"-\" / \".\" / \"_\" / \":\" / %x80-EFFFD  ");
        out.println("context-symbol = \"!\" / \"*\" / \"=\" / \"#\" / \"+\" / \"$\"");
        out.println("");
        out.println("LOWER          = %x61-7A");
        out.println("UPPER          = %x41-5A");
        out.println("HEX            = DIGIT / %x61-66");
        out.println("DIGIT          = %x30-39");
        out.println("NZDIG          = %x31-39");
        out.println("QUOTE          = %x22");
        out.println("ENDLINE        = %x0D / %x0A / ( %x0D %x0A )");
        out.println("");
        out.println("xdi-iri    = iri-scheme \":\" *iri-body");
        out.println("iri-scheme = LOWER *( LOWER / DIGIT ) ");
        out.println("iri-body   = ( \"(\" iri-body \")\" ) / *iri-char");
        out.println("iri-char   = xdi-name-char / context-symbol / iri-delim");
        out.println("iri-delim  = \"/\" / \"?\" / \"#\" / \"[\" / \"]\" / \"'\" / \",\" ");
        out.println("");
        out.println("json-value  = json-object / json-array / json-string / json-number / \"true\" / \"false\" / \"null\"");
        out.println("json-object = \"{\" [ json-string \":\" json-value *( \",\" json-string \":\" json-value ) ] \"}\"");
        out.println("json-array  = \"[\" [                 json-value *( \",\"                 json-value ) ] \"]\"");
        out.println("json-string = QUOTE *( \" \" / \"!\" / %x23-5B  / %x5D-EFFFD / json-escape )    QUOTE");
        out.println("json-escape = \"\\\"    (\"\\\" / \"/\" / \"b\" / \"f\" / \"n\" / \"r\" / \"t\" / QUOTE / ( \"u\" 4HEX ) )");
        out.println("json-number = ( \"0\" / [ \"-\" ] noleading ) [ \".\" notrailing ] [ \"E\" [ \"-\" ] noleading ]");
        out.println("noleading   = %x31-39 *DIGIT");
        out.println("notrailing  = *DIGIT  %x31-39");
    }
}
