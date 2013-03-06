package xdi2.core.xri3.parser.full.apg;

import com.coasttocoastresearch.apg.Grammar;
import java.io.PrintStream;

public class XDI3Grammar extends Grammar{

    // public API
    public static Grammar getInstance(){
        if(factoryInstance == null){
            factoryInstance = new XDI3Grammar(getRules(), getUdts(), getOpcodes());
        }
        return factoryInstance;
    }

    // rule name enum
    public static int ruleCount = 61;
    public enum RuleNames{
        ABSOLUTE("absolute", 5, 23, 3),
        ABSOLUTE_INVERSE("absolute-inverse", 9, 40, 3),
        ALPHA("ALPHA", 58, 298, 3),
        ATTRIBUTE_MEMBER("attribute-member", 34, 166, 4),
        ATTRIBUTE_SINGLETON("attribute-singleton", 37, 177, 4),
        COLLECTION("collection", 31, 155, 3),
        COLLECTION_CONTEXT("collection-context", 23, 102, 12),
        COLLECTION_INVERSE("collection-inverse", 12, 51, 6),
        COLLECTION_RELATIVE("collection-relative", 8, 34, 6),
        CONTEXT("context", 19, 86, 4),
        CONTEXT_INVERSE("context-inverse", 11, 47, 4),
        CONTEXT_RELATIVE("context-relative", 7, 30, 4),
        CONTEXTUAL("contextual", 2, 10, 3),
        DATA_IRI("data-iri", 50, 237, 4),
        DATA_XREF("data-xref", 49, 233, 4),
        DIGIT("DIGIT", 59, 301, 1),
        DIRECT("direct", 3, 13, 5),
        DIRECT_RELATION("direct-relation", 14, 60, 6),
        ENTITY_MEMBER("entity-member", 33, 162, 4),
        ENTITY_SINGLETON("entity-singleton", 38, 181, 3),
        GENERIC("generic", 44, 200, 7),
        GLOBAL("global", 39, 184, 3),
        HEXDIG("HEXDIG", 60, 302, 8),
        IMMUTABLE("immutable", 47, 221, 6),
        INNER_ROOT("inner-root", 27, 140, 4),
        INSTANCE("instance", 42, 193, 3),
        INVERSE("inverse", 4, 18, 5),
        INVERSE_RELATION("inverse-relation", 15, 66, 6),
        IRI_CHAR("iri-char", 52, 250, 3),
        IRI_LITERAL("iri-literal", 28, 144, 5),
        IRI_SCHEME("iri-scheme", 51, 241, 9),
        IUNRESERVED("iunreserved", 55, 268, 8),
        LITERAL("literal", 16, 72, 3),
        LITERAL_CONTEXT("literal-context", 25, 118, 13),
        LITERAL_REF("literal-ref", 18, 81, 5),
        LITERAL_VALUE("literal-value", 17, 75, 6),
        LOCAL("local", 40, 187, 3),
        MEMBER("member", 32, 158, 4),
        MEMBER_CONTEXT("member-context", 24, 114, 4),
        MUTABLE("mutable", 48, 227, 6),
        NONPAREN_DELIM("nonparen-delim", 53, 253, 12),
        ORDER_REF("order-ref", 35, 170, 4),
        ORGANIZATION("organization", 46, 214, 7),
        PCT_ENCODED("pct-encoded", 56, 276, 4),
        PEER("peer", 20, 90, 2),
        PEER_INVERSE("peer-inverse", 10, 43, 4),
        PEER_RELATIVE("peer-relative", 6, 26, 4),
        PEER_RELATIVE_CONTEXT("peer-relative-context", 21, 92, 3),
        PERSON("person", 45, 207, 7),
        RELATIONAL("relational", 13, 57, 3),
        RELATIVE_CONTEXT("relative-context", 22, 95, 7),
        SINGLETON("singleton", 36, 174, 3),
        SPECIFIC("specific", 43, 196, 4),
        SUBGRAPH("subgraph", 30, 151, 4),
        TYPE("type", 41, 190, 3),
        UCSCHAR("ucschar", 57, 280, 18),
        XDI_CHAR("xdi-char", 54, 265, 3),
        XDI_GRAPH("xdi-graph", 0, 0, 6),
        XDI_LITERAL("xdi-literal", 29, 149, 2),
        XDI_STATEMENT("xdi-statement", 1, 6, 4),
        XREF("xref", 26, 131, 9);
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
    private static XDI3Grammar factoryInstance = null;
    private XDI3Grammar(Rule[] rules, Udt[] udts, Opcode[] opcodes){
        super(rules, udts, opcodes);
    }

    private static Rule[] getRules(){
    	Rule[] rules = new Rule[61];
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
    	Opcode[] op = new Opcode[310];
        {int[] a = {1,2}; op[0] = getOpcodeCat(a);}
        op[1] = getOpcodeRnm(1, 6); // xdi-statement
        op[2] = getOpcodeRep((char)0, Character.MAX_VALUE, 3);
        {int[] a = {4,5}; op[3] = getOpcodeCat(a);}
        {char[] a = {10}; op[4] = getOpcodeTbs(a);}
        op[5] = getOpcodeRnm(1, 6); // xdi-statement
        {int[] a = {7,8,9}; op[6] = getOpcodeAlt(a);}
        op[7] = getOpcodeRnm(2, 10); // contextual
        op[8] = getOpcodeRnm(13, 57); // relational
        op[9] = getOpcodeRnm(16, 72); // literal
        {int[] a = {11,12}; op[10] = getOpcodeAlt(a);}
        op[11] = getOpcodeRnm(3, 13); // direct
        op[12] = getOpcodeRnm(4, 18); // inverse
        {int[] a = {14,15,16,17}; op[13] = getOpcodeAlt(a);}
        op[14] = getOpcodeRnm(5, 23); // absolute
        op[15] = getOpcodeRnm(6, 26); // peer-relative
        op[16] = getOpcodeRnm(7, 30); // context-relative
        op[17] = getOpcodeRnm(8, 34); // collection-relative
        {int[] a = {19,20,21,22}; op[18] = getOpcodeAlt(a);}
        op[19] = getOpcodeRnm(9, 40); // absolute-inverse
        op[20] = getOpcodeRnm(10, 43); // peer-inverse
        op[21] = getOpcodeRnm(11, 47); // context-inverse
        op[22] = getOpcodeRnm(12, 51); // collection-inverse
        {int[] a = {24,25}; op[23] = getOpcodeCat(a);}
        {char[] a = {40,41,47,40,41,47}; op[24] = getOpcodeTls(a);}
        op[25] = getOpcodeRnm(19, 86); // context
        {int[] a = {27,28,29}; op[26] = getOpcodeCat(a);}
        op[27] = getOpcodeRnm(20, 90); // peer
        {char[] a = {47,40,41,47}; op[28] = getOpcodeTls(a);}
        op[29] = getOpcodeRnm(19, 86); // context
        {int[] a = {31,32,33}; op[30] = getOpcodeCat(a);}
        op[31] = getOpcodeRnm(19, 86); // context
        {char[] a = {47,40,41,47}; op[32] = getOpcodeTls(a);}
        op[33] = getOpcodeRnm(22, 95); // relative-context
        {int[] a = {35,37,38,39}; op[34] = getOpcodeCat(a);}
        op[35] = getOpcodeRep((char)0, (char)1, 36);
        op[36] = getOpcodeRnm(20, 90); // peer
        op[37] = getOpcodeRnm(23, 102); // collection-context
        {char[] a = {47,40,41,47}; op[38] = getOpcodeTls(a);}
        op[39] = getOpcodeRnm(24, 114); // member-context
        {int[] a = {41,42}; op[40] = getOpcodeCat(a);}
        op[41] = getOpcodeRnm(19, 86); // context
        {char[] a = {47,36,105,115,40,41,47,40,41}; op[42] = getOpcodeTls(a);}
        {int[] a = {44,45,46}; op[43] = getOpcodeCat(a);}
        op[44] = getOpcodeRnm(19, 86); // context
        {char[] a = {47,36,105,115,40,41,47}; op[45] = getOpcodeTls(a);}
        op[46] = getOpcodeRnm(20, 90); // peer
        {int[] a = {48,49,50}; op[47] = getOpcodeCat(a);}
        op[48] = getOpcodeRnm(22, 95); // relative-context
        {char[] a = {47,36,105,115,40,41,47}; op[49] = getOpcodeTls(a);}
        op[50] = getOpcodeRnm(19, 86); // context
        {int[] a = {52,53,54,56}; op[51] = getOpcodeCat(a);}
        op[52] = getOpcodeRnm(24, 114); // member-context
        {char[] a = {47,36,105,115,40,41,47}; op[53] = getOpcodeTls(a);}
        op[54] = getOpcodeRep((char)0, (char)1, 55);
        op[55] = getOpcodeRnm(20, 90); // peer
        op[56] = getOpcodeRnm(23, 102); // collection-context
        {int[] a = {58,59}; op[57] = getOpcodeAlt(a);}
        op[58] = getOpcodeRnm(14, 60); // direct-relation
        op[59] = getOpcodeRnm(15, 66); // inverse-relation
        {int[] a = {61,62,63,64,65}; op[60] = getOpcodeCat(a);}
        op[61] = getOpcodeRnm(19, 86); // context
        {char[] a = {47}; op[62] = getOpcodeTls(a);}
        op[63] = getOpcodeRnm(19, 86); // context
        {char[] a = {47}; op[64] = getOpcodeTls(a);}
        op[65] = getOpcodeRnm(19, 86); // context
        {int[] a = {67,68,69,70,71}; op[66] = getOpcodeCat(a);}
        op[67] = getOpcodeRnm(19, 86); // context
        {char[] a = {47,36,105,115}; op[68] = getOpcodeTls(a);}
        op[69] = getOpcodeRnm(19, 86); // context
        {char[] a = {47}; op[70] = getOpcodeTls(a);}
        op[71] = getOpcodeRnm(19, 86); // context
        {int[] a = {73,74}; op[72] = getOpcodeAlt(a);}
        op[73] = getOpcodeRnm(17, 75); // literal-value
        op[74] = getOpcodeRnm(18, 81); // literal-ref
        {int[] a = {76,78,79,80}; op[75] = getOpcodeCat(a);}
        op[76] = getOpcodeRep((char)0, (char)1, 77);
        op[77] = getOpcodeRnm(20, 90); // peer
        op[78] = getOpcodeRnm(25, 118); // literal-context
        {char[] a = {47,33,47}; op[79] = getOpcodeTls(a);}
        op[80] = getOpcodeRnm(49, 233); // data-xref
        {int[] a = {82,84,85}; op[81] = getOpcodeCat(a);}
        op[82] = getOpcodeRep((char)0, (char)1, 83);
        op[83] = getOpcodeRnm(20, 90); // peer
        op[84] = getOpcodeRnm(25, 118); // literal-context
        {char[] a = {47,33,47,40,41}; op[85] = getOpcodeTls(a);}
        {int[] a = {87,88,89}; op[86] = getOpcodeAlt(a);}
        op[87] = getOpcodeRnm(20, 90); // peer
        op[88] = getOpcodeRnm(21, 92); // peer-relative-context
        op[89] = getOpcodeRnm(22, 95); // relative-context
        op[90] = getOpcodeRep((char)1, Character.MAX_VALUE, 91);
        op[91] = getOpcodeRnm(26, 131); // xref
        {int[] a = {93,94}; op[92] = getOpcodeCat(a);}
        op[93] = getOpcodeRnm(20, 90); // peer
        op[94] = getOpcodeRnm(22, 95); // relative-context
        op[95] = getOpcodeRep((char)1, Character.MAX_VALUE, 96);
        {int[] a = {97,98}; op[96] = getOpcodeAlt(a);}
        op[97] = getOpcodeRnm(36, 174); // singleton
        {int[] a = {99,100}; op[98] = getOpcodeCat(a);}
        op[99] = getOpcodeRnm(31, 155); // collection
        op[100] = getOpcodeRep((char)0, (char)1, 101);
        op[101] = getOpcodeRnm(32, 158); // member
        {int[] a = {103,106,109,113}; op[102] = getOpcodeAlt(a);}
        {int[] a = {104,105}; op[103] = getOpcodeCat(a);}
        op[104] = getOpcodeRnm(36, 174); // singleton
        op[105] = getOpcodeRnm(23, 102); // collection-context
        {int[] a = {107,108}; op[106] = getOpcodeCat(a);}
        op[107] = getOpcodeRnm(31, 155); // collection
        op[108] = getOpcodeRnm(23, 102); // collection-context
        {int[] a = {110,111,112}; op[109] = getOpcodeCat(a);}
        op[110] = getOpcodeRnm(31, 155); // collection
        op[111] = getOpcodeRnm(32, 158); // member
        op[112] = getOpcodeRnm(23, 102); // collection-context
        op[113] = getOpcodeRnm(31, 155); // collection
        {int[] a = {115,116}; op[114] = getOpcodeCat(a);}
        op[115] = getOpcodeRnm(32, 158); // member
        op[116] = getOpcodeRep((char)0, (char)1, 117);
        op[117] = getOpcodeRnm(22, 95); // relative-context
        {int[] a = {119,122,125,129,130}; op[118] = getOpcodeAlt(a);}
        {int[] a = {120,121}; op[119] = getOpcodeCat(a);}
        op[120] = getOpcodeRnm(36, 174); // singleton
        op[121] = getOpcodeRnm(25, 118); // literal-context
        {int[] a = {123,124}; op[122] = getOpcodeCat(a);}
        op[123] = getOpcodeRnm(31, 155); // collection
        op[124] = getOpcodeRnm(25, 118); // literal-context
        {int[] a = {126,127,128}; op[125] = getOpcodeCat(a);}
        op[126] = getOpcodeRnm(31, 155); // collection
        op[127] = getOpcodeRnm(32, 158); // member
        op[128] = getOpcodeRnm(25, 118); // literal-context
        op[129] = getOpcodeRnm(34, 166); // attribute-member
        op[130] = getOpcodeRnm(37, 177); // attribute-singleton
        {int[] a = {132,133,139}; op[131] = getOpcodeCat(a);}
        {char[] a = {40}; op[132] = getOpcodeTls(a);}
        {int[] a = {134,135,136,137,138}; op[133] = getOpcodeAlt(a);}
        op[134] = getOpcodeRnm(19, 86); // context
        op[135] = getOpcodeRnm(27, 140); // inner-root
        op[136] = getOpcodeRnm(1, 6); // xdi-statement
        op[137] = getOpcodeRnm(28, 144); // iri-literal
        op[138] = getOpcodeRnm(29, 149); // xdi-literal
        {char[] a = {41}; op[139] = getOpcodeTls(a);}
        {int[] a = {141,142,143}; op[140] = getOpcodeCat(a);}
        op[141] = getOpcodeRnm(19, 86); // context
        {char[] a = {47}; op[142] = getOpcodeTls(a);}
        op[143] = getOpcodeRnm(19, 86); // context
        {int[] a = {145,146,147}; op[144] = getOpcodeCat(a);}
        op[145] = getOpcodeRnm(51, 241); // iri-scheme
        {char[] a = {58}; op[146] = getOpcodeTls(a);}
        op[147] = getOpcodeRep((char)0, Character.MAX_VALUE, 148);
        op[148] = getOpcodeRnm(52, 250); // iri-char
        op[149] = getOpcodeRep((char)1, Character.MAX_VALUE, 150);
        op[150] = getOpcodeRnm(54, 265); // xdi-char
        {int[] a = {152,153,154}; op[151] = getOpcodeAlt(a);}
        op[152] = getOpcodeRnm(31, 155); // collection
        op[153] = getOpcodeRnm(32, 158); // member
        op[154] = getOpcodeRnm(36, 174); // singleton
        {int[] a = {156,157}; op[155] = getOpcodeCat(a);}
        {char[] a = {36}; op[156] = getOpcodeTls(a);}
        op[157] = getOpcodeRnm(26, 131); // xref
        {int[] a = {159,160,161}; op[158] = getOpcodeAlt(a);}
        op[159] = getOpcodeRnm(33, 162); // entity-member
        op[160] = getOpcodeRnm(34, 166); // attribute-member
        op[161] = getOpcodeRnm(35, 170); // order-ref
        {int[] a = {163,164,165}; op[162] = getOpcodeCat(a);}
        {char[] a = {36,40}; op[163] = getOpcodeTls(a);}
        op[164] = getOpcodeRnm(47, 221); // immutable
        {char[] a = {41}; op[165] = getOpcodeTls(a);}
        {int[] a = {167,168,169}; op[166] = getOpcodeCat(a);}
        {char[] a = {36,33,40}; op[167] = getOpcodeTls(a);}
        op[168] = getOpcodeRnm(47, 221); // immutable
        {char[] a = {41}; op[169] = getOpcodeTls(a);}
        {int[] a = {171,172}; op[170] = getOpcodeCat(a);}
        {char[] a = {36,42}; op[171] = getOpcodeTls(a);}
        op[172] = getOpcodeRep((char)1, Character.MAX_VALUE, 173);
        op[173] = getOpcodeRnm(59, 301); // DIGIT
        {int[] a = {175,176}; op[174] = getOpcodeAlt(a);}
        op[175] = getOpcodeRnm(37, 177); // attribute-singleton
        op[176] = getOpcodeRnm(38, 181); // entity-singleton
        {int[] a = {178,179,180}; op[177] = getOpcodeCat(a);}
        {char[] a = {36,33,40}; op[178] = getOpcodeTls(a);}
        op[179] = getOpcodeRnm(41, 190); // type
        {char[] a = {41}; op[180] = getOpcodeTls(a);}
        {int[] a = {182,183}; op[181] = getOpcodeAlt(a);}
        op[182] = getOpcodeRnm(39, 184); // global
        op[183] = getOpcodeRnm(40, 187); // local
        {int[] a = {185,186}; op[184] = getOpcodeAlt(a);}
        op[185] = getOpcodeRnm(41, 190); // type
        op[186] = getOpcodeRnm(42, 193); // instance
        {int[] a = {188,189}; op[187] = getOpcodeAlt(a);}
        op[188] = getOpcodeRnm(47, 221); // immutable
        op[189] = getOpcodeRnm(48, 227); // mutable
        {int[] a = {191,192}; op[190] = getOpcodeAlt(a);}
        op[191] = getOpcodeRnm(43, 196); // specific
        op[192] = getOpcodeRnm(44, 200); // generic
        {int[] a = {194,195}; op[193] = getOpcodeAlt(a);}
        op[194] = getOpcodeRnm(45, 207); // person
        op[195] = getOpcodeRnm(46, 214); // organization
        {int[] a = {197,198}; op[196] = getOpcodeCat(a);}
        {char[] a = {36}; op[197] = getOpcodeTls(a);}
        op[198] = getOpcodeRep((char)0, (char)1, 199);
        op[199] = getOpcodeRnm(29, 149); // xdi-literal
        {int[] a = {201,202}; op[200] = getOpcodeCat(a);}
        {char[] a = {43}; op[201] = getOpcodeTls(a);}
        op[202] = getOpcodeRep((char)0, (char)1, 203);
        {int[] a = {204,205,206}; op[203] = getOpcodeAlt(a);}
        op[204] = getOpcodeRnm(40, 187); // local
        op[205] = getOpcodeRnm(26, 131); // xref
        op[206] = getOpcodeRnm(29, 149); // xdi-literal
        {int[] a = {208,209}; op[207] = getOpcodeCat(a);}
        {char[] a = {61}; op[208] = getOpcodeTls(a);}
        op[209] = getOpcodeRep((char)0, (char)1, 210);
        {int[] a = {211,212,213}; op[210] = getOpcodeAlt(a);}
        op[211] = getOpcodeRnm(40, 187); // local
        op[212] = getOpcodeRnm(26, 131); // xref
        op[213] = getOpcodeRnm(29, 149); // xdi-literal
        {int[] a = {215,216}; op[214] = getOpcodeCat(a);}
        {char[] a = {64}; op[215] = getOpcodeTls(a);}
        op[216] = getOpcodeRep((char)0, (char)1, 217);
        {int[] a = {218,219,220}; op[217] = getOpcodeAlt(a);}
        op[218] = getOpcodeRnm(40, 187); // local
        op[219] = getOpcodeRnm(26, 131); // xref
        op[220] = getOpcodeRnm(29, 149); // xdi-literal
        {int[] a = {222,223}; op[221] = getOpcodeCat(a);}
        {char[] a = {33}; op[222] = getOpcodeTls(a);}
        op[223] = getOpcodeRep((char)0, (char)1, 224);
        {int[] a = {225,226}; op[224] = getOpcodeAlt(a);}
        op[225] = getOpcodeRnm(26, 131); // xref
        op[226] = getOpcodeRnm(29, 149); // xdi-literal
        {int[] a = {228,229}; op[227] = getOpcodeCat(a);}
        {char[] a = {42}; op[228] = getOpcodeTls(a);}
        op[229] = getOpcodeRep((char)0, (char)1, 230);
        {int[] a = {231,232}; op[230] = getOpcodeAlt(a);}
        op[231] = getOpcodeRnm(26, 131); // xref
        op[232] = getOpcodeRnm(29, 149); // xdi-literal
        {int[] a = {234,235,236}; op[233] = getOpcodeCat(a);}
        {char[] a = {40}; op[234] = getOpcodeTls(a);}
        op[235] = getOpcodeRnm(50, 237); // data-iri
        {char[] a = {41}; op[236] = getOpcodeTls(a);}
        {int[] a = {238,239}; op[237] = getOpcodeCat(a);}
        {char[] a = {100,97,116,97,58,44}; op[238] = getOpcodeTls(a);}
        op[239] = getOpcodeRep((char)1, Character.MAX_VALUE, 240);
        op[240] = getOpcodeRnm(52, 250); // iri-char
        {int[] a = {242,243}; op[241] = getOpcodeCat(a);}
        op[242] = getOpcodeRnm(58, 298); // ALPHA
        op[243] = getOpcodeRep((char)0, Character.MAX_VALUE, 244);
        {int[] a = {245,246,247,248,249}; op[244] = getOpcodeAlt(a);}
        op[245] = getOpcodeRnm(58, 298); // ALPHA
        op[246] = getOpcodeRnm(59, 301); // DIGIT
        {char[] a = {43}; op[247] = getOpcodeTls(a);}
        {char[] a = {45}; op[248] = getOpcodeTls(a);}
        {char[] a = {46}; op[249] = getOpcodeTls(a);}
        {int[] a = {251,252}; op[250] = getOpcodeAlt(a);}
        op[251] = getOpcodeRnm(54, 265); // xdi-char
        op[252] = getOpcodeRnm(53, 253); // nonparen-delim
        {int[] a = {254,255,256,257,258,259,260,261,262,263,264}; op[253] = getOpcodeAlt(a);}
        {char[] a = {58}; op[254] = getOpcodeTls(a);}
        {char[] a = {47}; op[255] = getOpcodeTls(a);}
        {char[] a = {63}; op[256] = getOpcodeTls(a);}
        {char[] a = {35}; op[257] = getOpcodeTls(a);}
        {char[] a = {91}; op[258] = getOpcodeTls(a);}
        {char[] a = {93}; op[259] = getOpcodeTls(a);}
        {char[] a = {64}; op[260] = getOpcodeTls(a);}
        {char[] a = {33}; op[261] = getOpcodeTls(a);}
        {char[] a = {36}; op[262] = getOpcodeTls(a);}
        {char[] a = {38}; op[263] = getOpcodeTls(a);}
        {char[] a = {39}; op[264] = getOpcodeTls(a);}
        {int[] a = {266,267}; op[265] = getOpcodeAlt(a);}
        op[266] = getOpcodeRnm(55, 268); // iunreserved
        op[267] = getOpcodeRnm(56, 276); // pct-encoded
        {int[] a = {269,270,271,272,273,274,275}; op[268] = getOpcodeAlt(a);}
        op[269] = getOpcodeRnm(58, 298); // ALPHA
        op[270] = getOpcodeRnm(59, 301); // DIGIT
        {char[] a = {45}; op[271] = getOpcodeTls(a);}
        {char[] a = {46}; op[272] = getOpcodeTls(a);}
        {char[] a = {95}; op[273] = getOpcodeTls(a);}
        {char[] a = {126}; op[274] = getOpcodeTls(a);}
        op[275] = getOpcodeRnm(57, 280); // ucschar
        {int[] a = {277,278,279}; op[276] = getOpcodeCat(a);}
        {char[] a = {37}; op[277] = getOpcodeTls(a);}
        op[278] = getOpcodeRnm(60, 302); // HEXDIG
        op[279] = getOpcodeRnm(60, 302); // HEXDIG
        {int[] a = {281,282,283,284,285,286,287,288,289,290,291,292,293,294,295,296,297}; op[280] = getOpcodeAlt(a);}
        op[281] = getOpcodeTrg((char)160, (char)55295);
        op[282] = getOpcodeTrg((char)63744, (char)64975);
        op[283] = getOpcodeTrg((char)65008, (char)65519);
        op[284] = getOpcodeTrg((char)0, (char)65533);
        op[285] = getOpcodeTrg((char)0, (char)65533);
        op[286] = getOpcodeTrg((char)0, (char)65533);
        op[287] = getOpcodeTrg((char)0, (char)65533);
        op[288] = getOpcodeTrg((char)0, (char)65533);
        op[289] = getOpcodeTrg((char)0, (char)65533);
        op[290] = getOpcodeTrg((char)0, (char)65533);
        op[291] = getOpcodeTrg((char)0, (char)65533);
        op[292] = getOpcodeTrg((char)0, (char)65533);
        op[293] = getOpcodeTrg((char)0, (char)65533);
        op[294] = getOpcodeTrg((char)0, (char)65533);
        op[295] = getOpcodeTrg((char)0, (char)65533);
        op[296] = getOpcodeTrg((char)0, (char)65533);
        op[297] = getOpcodeTrg((char)4096, (char)65533);
        {int[] a = {299,300}; op[298] = getOpcodeAlt(a);}
        op[299] = getOpcodeTrg((char)65, (char)90);
        op[300] = getOpcodeTrg((char)97, (char)122);
        op[301] = getOpcodeTrg((char)48, (char)57);
        {int[] a = {303,304,305,306,307,308,309}; op[302] = getOpcodeAlt(a);}
        op[303] = getOpcodeRnm(59, 301); // DIGIT
        {char[] a = {65}; op[304] = getOpcodeTls(a);}
        {char[] a = {66}; op[305] = getOpcodeTls(a);}
        {char[] a = {67}; op[306] = getOpcodeTls(a);}
        {char[] a = {68}; op[307] = getOpcodeTls(a);}
        {char[] a = {69}; op[308] = getOpcodeTls(a);}
        {char[] a = {70}; op[309] = getOpcodeTls(a);}
        return op;
    }

    public static void display(PrintStream out){
        out.println(";");
        out.println("; xdi2.core.xri3.parser.apg.XDI3Grammar");
        out.println(";");
        out.println("xdi-graph               = xdi-statement *( %x0A xdi-statement )");
        out.println("xdi-statement           = contextual / relational / literal");
        out.println("");
        out.println("contextual              = direct / inverse");
        out.println("direct                  = absolute / peer-relative / context-relative / collection-relative");
        out.println("inverse                 = absolute-inverse / peer-inverse / context-inverse / collection-inverse");
        out.println("absolute                = \"()/()/\" context");
        out.println("peer-relative           = peer \"/()/\" context");
        out.println("context-relative        = context \"/()/\" relative-context");
        out.println("collection-relative     = [ peer ] collection-context \"/()/\" member-context");
        out.println("absolute-inverse        = context \"/$is()/()\"");
        out.println("peer-inverse            = context \"/$is()/\" peer");
        out.println("context-inverse         = relative-context \"/$is()/\" context");
        out.println("collection-inverse      = member-context \"/$is()/\" [ peer ] collection-context");
        out.println("");
        out.println("relational              = direct-relation / inverse-relation");
        out.println("direct-relation         = context \"/\" context \"/\" context");
        out.println("inverse-relation        = context \"/$is\" context \"/\" context");
        out.println("");
        out.println("literal                 = literal-value / literal-ref");
        out.println("literal-value           = [ peer ] literal-context \"/!/\" data-xref");
        out.println("literal-ref             = [ peer ] literal-context \"/!/()\"");
        out.println("");
        out.println("context                 = peer / peer-relative-context / relative-context");
        out.println("peer                    = 1*xref");
        out.println("peer-relative-context   = peer relative-context");
        out.println("relative-context        = 1*( singleton / ( collection [ member ] ) )");
        out.println("");
        out.println("collection-context      = ( singleton collection-context ) / ( collection collection-context ) / ( collection member collection-context ) / collection");
        out.println("");
        out.println("member-context          = member [ relative-context ]");
        out.println("");
        out.println("literal-context         = ( singleton literal-context ) / ( collection literal-context ) / ( collection member literal-context ) / attribute-member / attribute-singleton");
        out.println("");
        out.println("xref                    = \"(\" ( context / inner-root / xdi-statement / iri-literal / xdi-literal ) \")\"");
        out.println("inner-root              = context \"/\" context");
        out.println("iri-literal             = iri-scheme \":\" *iri-char");
        out.println("xdi-literal             = 1*xdi-char");
        out.println("");
        out.println("subgraph                = collection / member / singleton");
        out.println("");
        out.println("collection              = \"$\" xref");
        out.println("");
        out.println("member                  = entity-member / attribute-member / order-ref");
        out.println("entity-member           = \"$(\" immutable \")\"");
        out.println("attribute-member        = \"$!(\" immutable \")\"");
        out.println("order-ref               = \"$*\" 1*DIGIT");
        out.println("");
        out.println("singleton               = attribute-singleton / entity-singleton");
        out.println("");
        out.println("attribute-singleton     = \"$!(\" type \")\"");
        out.println("");
        out.println("entity-singleton        = global / local");
        out.println("global                  = type / instance");
        out.println("local                   = immutable / mutable");
        out.println("type                    = specific / generic");
        out.println("instance                = person / organization");
        out.println("specific                = \"$\" [ xdi-literal ]");
        out.println("generic                 = \"+\" [ local / xref / xdi-literal ]");
        out.println("person                  = \"=\" [ local / xref / xdi-literal ]");
        out.println("organization            = \"@\" [ local / xref / xdi-literal ]");
        out.println("immutable               = \"!\" [ xref / xdi-literal ]");
        out.println("mutable                 = \"*\" [ xref / xdi-literal ]");
        out.println("");
        out.println("data-xref               = \"(\" data-iri \")\"");
        out.println("data-iri                = \"data:,\" 1*iri-char");
        out.println("");
        out.println("iri-scheme              = ALPHA *( ALPHA / DIGIT / \"+\" / \"-\" / \".\" )  ");
        out.println("iri-char                = xdi-char / nonparen-delim                                          ; \"(\" and \")\" are excluded");
        out.println("nonparen-delim          = \":\" / \"/\" / \"?\" / \"#\" / \"[\" / \"]\" / \"@\" / \"!\" / \"$\" / \"&\" / \"'\"");
        out.println("");
        out.println("xdi-char                = iunreserved / pct-encoded");
        out.println("iunreserved             = ALPHA / DIGIT / \"-\" / \".\" / \"_\" / \"~\" / ucschar");
        out.println("pct-encoded             = \"%\" HEXDIG HEXDIG");
        out.println("ucschar                 = %xA0-D7FF / %xF900-FDCF / %xFDF0-FFEF");
        out.println("                        / %x10000-1FFFD / %x20000-2FFFD / %x30000-3FFFD");
        out.println("                        / %x40000-4FFFD / %x50000-5FFFD / %x60000-6FFFD");
        out.println("                        / %x70000-7FFFD / %x80000-8FFFD / %x90000-9FFFD");
        out.println("                        / %xA0000-AFFFD / %xB0000-BFFFD / %xC0000-CFFFD");
        out.println("                        / %xD0000-DFFFD / %xE1000-EFFFD");
        out.println("ALPHA                   =  %x41-5A / %x61-7A   ; A-Z / a-z");
        out.println("DIGIT                   =  %x30-39     ; 0-9");
        out.println("HEXDIG                  =  DIGIT / \"A\" / \"B\" / \"C\" / \"D\" / \"E\" / \"F\"");
    }
}
