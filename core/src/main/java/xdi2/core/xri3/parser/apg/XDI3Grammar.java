package xdi2.core.xri3.parser.apg;

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
    public static int ruleCount = 72;
    public enum RuleNames{
        ABSOLUTE("absolute", 5, 23, 3),
        ABSOLUTE_INVERSE("absolute-inverse", 9, 38, 3),
        ALPHA("ALPHA", 67, 309, 3),
        ATTRIBUTE_CLASS("attribute-class", 45, 202, 4),
        ATTRIBUTE_PAIR("attribute-pair", 24, 106, 5),
        ATTRIBUTE_SINGLETON("attribute-singleton", 33, 154, 4),
        CLASS("class", 37, 176, 3),
        CLASS_CONTEXT("class-context", 19, 81, 4),
        CLASS_INVERSE("class-inverse", 12, 49, 4),
        CLASS_PATH("class-path", 20, 85, 5),
        CLASS_RELATIVE("class-relative", 8, 34, 4),
        CLOCK_SEQ("clock-seq", 59, 272, 2),
        CLOCK_SEQ_LOW("clock-seq-low", 60, 274, 2),
        CONTEXT("context", 17, 73, 6),
        CONTEXT_INVERSE("context-inverse", 11, 45, 4),
        CONTEXT_RELATIVE("context-relative", 7, 30, 4),
        CONTEXT_SYMBOL("context-symbol", 65, 294, 7),
        CONTEXTUAL_STATEMENT("contextual-statement", 2, 10, 3),
        CRLF("CRLF", 70, 316, 6),
        DIGIT("DIGIT", 68, 312, 1),
        DIRECT_CONTEXTUAL("direct-contextual", 3, 13, 5),
        DIRECT_RELATIONAL("direct-relational", 14, 56, 6),
        DQUOTE("DQUOTE", 71, 322, 1),
        ELEMENT("element", 47, 214, 5),
        ENTITY_CLASS("entity-class", 38, 179, 3),
        ENTITY_SINGLETON("entity-singleton", 32, 150, 4),
        GENERIC("generic", 42, 194, 6),
        HEXDIG("HEXDIG", 69, 313, 3),
        INNER_ROOT("inner-root", 28, 127, 4),
        INSTANCE("instance", 46, 206, 8),
        INSTANCE_CLASS("instance-class", 40, 185, 3),
        INSTANCE_CONTEXT("instance-context", 21, 90, 6),
        INVERSE_CONTEXTUAL("inverse-contextual", 4, 18, 5),
        INVERSE_RELATIONAL("inverse-relational", 15, 62, 6),
        IPV6_LITERAL("ipv6-literal", 54, 245, 9),
        IRI_CHARS("iri-chars", 63, 282, 4),
        JSON_ARRAY("json-array", 52, 235, 5),
        JSON_BOOLEAN("json-boolean", 51, 232, 3),
        JSON_NUMBER("json-number", 50, 230, 2),
        JSON_OBJECT("json-object", 53, 240, 5),
        JSON_STRING("json-string", 49, 225, 5),
        JSON_VALUE("json-value", 48, 219, 6),
        LITERAL_CONTEXT("literal-context", 22, 96, 4),
        LITERAL_PATH("literal-path", 23, 100, 6),
        LITERAL_STATEMENT("literal-statement", 16, 68, 5),
        NODE("node", 61, 276, 2),
        NONPAREN_DELIM("nonparen-delim", 64, 286, 8),
        ORGANIZATION("organization", 44, 201, 1),
        ORGANIZATION_SINGLETON("organization-singleton", 35, 163, 5),
        PEER("peer", 26, 116, 2),
        PEER_INVERSE("peer-inverse", 10, 41, 4),
        PEER_RELATIVE("peer-relative", 6, 26, 4),
        PERSON("person", 43, 200, 1),
        PERSON_SINGLETON("person-singleton", 34, 158, 5),
        RELATIONAL_STATEMENT("relational-statement", 13, 53, 3),
        RELATIVE_CONTEXT("relative-context", 18, 79, 2),
        RELATIVE_SINGLETON("relative-singleton", 36, 168, 8),
        SINGLETON("singleton", 31, 144, 6),
        SPECIFIC("specific", 41, 188, 6),
        SUBPATH("subpath", 29, 131, 8),
        SUBSEGMENT("subsegment", 30, 139, 5),
        TIME_HIGH("time-high", 58, 270, 2),
        TIME_LOW("time-low", 56, 266, 2),
        TIME_MID("time-mid", 57, 268, 2),
        TYPE_CLASS("type-class", 39, 182, 3),
        UUID_LITERAL("uuid-literal", 55, 254, 12),
        VALUE_CONTEXT("value-context", 25, 111, 5),
        XDI_CHAR("xdi-char", 66, 301, 8),
        XDI_CHARS("xdi-chars", 62, 278, 4),
        XDI_GRAPH("xdi-graph", 0, 0, 6),
        XDI_STATEMENT("xdi-statement", 1, 6, 4),
        XREF("xref", 27, 118, 9);
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
    	Rule[] rules = new Rule[72];
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
    	Opcode[] op = new Opcode[323];
        {int[] a = {1,2}; op[0] = getOpcodeCat(a);}
        op[1] = getOpcodeRnm(1, 6); // xdi-statement
        op[2] = getOpcodeRep((char)0, Character.MAX_VALUE, 3);
        {int[] a = {4,5}; op[3] = getOpcodeCat(a);}
        op[4] = getOpcodeRnm(70, 316); // CRLF
        op[5] = getOpcodeRnm(1, 6); // xdi-statement
        {int[] a = {7,8,9}; op[6] = getOpcodeAlt(a);}
        op[7] = getOpcodeRnm(2, 10); // contextual-statement
        op[8] = getOpcodeRnm(13, 53); // relational-statement
        op[9] = getOpcodeRnm(16, 68); // literal-statement
        {int[] a = {11,12}; op[10] = getOpcodeAlt(a);}
        op[11] = getOpcodeRnm(3, 13); // direct-contextual
        op[12] = getOpcodeRnm(4, 18); // inverse-contextual
        {int[] a = {14,15,16,17}; op[13] = getOpcodeAlt(a);}
        op[14] = getOpcodeRnm(5, 23); // absolute
        op[15] = getOpcodeRnm(6, 26); // peer-relative
        op[16] = getOpcodeRnm(7, 30); // context-relative
        op[17] = getOpcodeRnm(8, 34); // class-relative
        {int[] a = {19,20,21,22}; op[18] = getOpcodeAlt(a);}
        op[19] = getOpcodeRnm(9, 38); // absolute-inverse
        op[20] = getOpcodeRnm(10, 41); // peer-inverse
        op[21] = getOpcodeRnm(11, 45); // context-inverse
        op[22] = getOpcodeRnm(12, 49); // class-inverse
        {int[] a = {24,25}; op[23] = getOpcodeCat(a);}
        {char[] a = {40,41,47,40,41,47}; op[24] = getOpcodeTls(a);}
        op[25] = getOpcodeRnm(17, 73); // context
        {int[] a = {27,28,29}; op[26] = getOpcodeCat(a);}
        op[27] = getOpcodeRnm(26, 116); // peer
        {char[] a = {47,40,41,47}; op[28] = getOpcodeTls(a);}
        op[29] = getOpcodeRnm(17, 73); // context
        {int[] a = {31,32,33}; op[30] = getOpcodeCat(a);}
        op[31] = getOpcodeRnm(17, 73); // context
        {char[] a = {47,40,41,47}; op[32] = getOpcodeTls(a);}
        op[33] = getOpcodeRnm(18, 79); // relative-context
        {int[] a = {35,36,37}; op[34] = getOpcodeCat(a);}
        op[35] = getOpcodeRnm(19, 81); // class-context
        {char[] a = {47,40,41,47}; op[36] = getOpcodeTls(a);}
        op[37] = getOpcodeRnm(21, 90); // instance-context
        {int[] a = {39,40}; op[38] = getOpcodeCat(a);}
        op[39] = getOpcodeRnm(17, 73); // context
        {char[] a = {47,36,105,115,40,41,47,40,41}; op[40] = getOpcodeTls(a);}
        {int[] a = {42,43,44}; op[41] = getOpcodeCat(a);}
        op[42] = getOpcodeRnm(17, 73); // context
        {char[] a = {47,36,105,115,40,41,47}; op[43] = getOpcodeTls(a);}
        op[44] = getOpcodeRnm(26, 116); // peer
        {int[] a = {46,47,48}; op[45] = getOpcodeCat(a);}
        op[46] = getOpcodeRnm(18, 79); // relative-context
        {char[] a = {47,36,105,115,40,41,47}; op[47] = getOpcodeTls(a);}
        op[48] = getOpcodeRnm(17, 73); // context
        {int[] a = {50,51,52}; op[49] = getOpcodeCat(a);}
        op[50] = getOpcodeRnm(21, 90); // instance-context
        {char[] a = {47,36,105,115,40,41,47}; op[51] = getOpcodeTls(a);}
        op[52] = getOpcodeRnm(19, 81); // class-context
        {int[] a = {54,55}; op[53] = getOpcodeAlt(a);}
        op[54] = getOpcodeRnm(15, 62); // inverse-relational
        op[55] = getOpcodeRnm(14, 56); // direct-relational
        {int[] a = {57,58,59,60,61}; op[56] = getOpcodeCat(a);}
        op[57] = getOpcodeRnm(17, 73); // context
        {char[] a = {47}; op[58] = getOpcodeTls(a);}
        op[59] = getOpcodeRnm(17, 73); // context
        {char[] a = {47}; op[60] = getOpcodeTls(a);}
        op[61] = getOpcodeRnm(17, 73); // context
        {int[] a = {63,64,65,66,67}; op[62] = getOpcodeCat(a);}
        op[63] = getOpcodeRnm(17, 73); // context
        {char[] a = {47,36,105,115}; op[64] = getOpcodeTls(a);}
        op[65] = getOpcodeRnm(17, 73); // context
        {char[] a = {47}; op[66] = getOpcodeTls(a);}
        op[67] = getOpcodeRnm(17, 73); // context
        {int[] a = {69,70,71,72}; op[68] = getOpcodeCat(a);}
        op[69] = getOpcodeRnm(22, 96); // literal-context
        op[70] = getOpcodeRnm(25, 111); // value-context
        {char[] a = {47,60,62,47}; op[71] = getOpcodeTls(a);}
        op[72] = getOpcodeRnm(48, 219); // json-value
        {int[] a = {74,78}; op[73] = getOpcodeAlt(a);}
        {int[] a = {75,76}; op[74] = getOpcodeCat(a);}
        op[75] = getOpcodeRnm(26, 116); // peer
        op[76] = getOpcodeRep((char)0, (char)1, 77);
        op[77] = getOpcodeRnm(18, 79); // relative-context
        op[78] = getOpcodeRnm(18, 79); // relative-context
        op[79] = getOpcodeRep((char)1, Character.MAX_VALUE, 80);
        op[80] = getOpcodeRnm(29, 131); // subpath
        {int[] a = {82,84}; op[81] = getOpcodeCat(a);}
        op[82] = getOpcodeRep((char)0, (char)1, 83);
        op[83] = getOpcodeRnm(26, 116); // peer
        op[84] = getOpcodeRnm(20, 85); // class-path
        {int[] a = {86,89}; op[85] = getOpcodeAlt(a);}
        {int[] a = {87,88}; op[86] = getOpcodeCat(a);}
        op[87] = getOpcodeRnm(29, 131); // subpath
        op[88] = getOpcodeRnm(20, 85); // class-path
        op[89] = getOpcodeRnm(37, 176); // class
        {int[] a = {91,94}; op[90] = getOpcodeCat(a);}
        {int[] a = {92,93}; op[91] = getOpcodeAlt(a);}
        op[92] = getOpcodeRnm(46, 206); // instance
        op[93] = getOpcodeRnm(47, 214); // element
        op[94] = getOpcodeRep((char)0, (char)1, 95);
        op[95] = getOpcodeRnm(18, 79); // relative-context
        {int[] a = {97,99}; op[96] = getOpcodeCat(a);}
        op[97] = getOpcodeRep((char)0, (char)1, 98);
        op[98] = getOpcodeRnm(26, 116); // peer
        op[99] = getOpcodeRnm(23, 100); // literal-path
        {int[] a = {101,104,105}; op[100] = getOpcodeAlt(a);}
        {int[] a = {102,103}; op[101] = getOpcodeCat(a);}
        op[102] = getOpcodeRnm(29, 131); // subpath
        op[103] = getOpcodeRnm(23, 100); // literal-path
        op[104] = getOpcodeRnm(33, 154); // attribute-singleton
        op[105] = getOpcodeRnm(24, 106); // attribute-pair
        {int[] a = {107,108}; op[106] = getOpcodeCat(a);}
        op[107] = getOpcodeRnm(45, 202); // attribute-class
        {int[] a = {109,110}; op[108] = getOpcodeAlt(a);}
        op[109] = getOpcodeRnm(46, 206); // instance
        op[110] = getOpcodeRnm(47, 214); // element
        {int[] a = {112,113,115}; op[111] = getOpcodeCat(a);}
        {char[] a = {60,60}; op[112] = getOpcodeTls(a);}
        op[113] = getOpcodeRep((char)1, Character.MAX_VALUE, 114);
        op[114] = getOpcodeRnm(39, 182); // type-class
        {char[] a = {62,62}; op[115] = getOpcodeTls(a);}
        op[116] = getOpcodeRep((char)1, Character.MAX_VALUE, 117);
        op[117] = getOpcodeRnm(27, 118); // xref
        {int[] a = {119,120,126}; op[118] = getOpcodeCat(a);}
        {char[] a = {40}; op[119] = getOpcodeTls(a);}
        {int[] a = {121,122,123,124,125}; op[120] = getOpcodeAlt(a);}
        op[121] = getOpcodeRnm(17, 73); // context
        op[122] = getOpcodeRnm(28, 127); // inner-root
        op[123] = getOpcodeRnm(1, 6); // xdi-statement
        op[124] = getOpcodeRnm(63, 282); // iri-chars
        op[125] = getOpcodeRnm(62, 278); // xdi-chars
        {char[] a = {41}; op[126] = getOpcodeTls(a);}
        {int[] a = {128,129,130}; op[127] = getOpcodeCat(a);}
        op[128] = getOpcodeRnm(17, 73); // context
        {char[] a = {47}; op[129] = getOpcodeTls(a);}
        op[130] = getOpcodeRnm(17, 73); // context
        {int[] a = {132,133}; op[131] = getOpcodeAlt(a);}
        op[132] = getOpcodeRnm(31, 144); // singleton
        {int[] a = {134,135}; op[133] = getOpcodeCat(a);}
        op[134] = getOpcodeRnm(37, 176); // class
        op[135] = getOpcodeRep((char)0, (char)1, 136);
        {int[] a = {137,138}; op[136] = getOpcodeAlt(a);}
        op[137] = getOpcodeRnm(46, 206); // instance
        op[138] = getOpcodeRnm(47, 214); // element
        {int[] a = {140,141,142,143}; op[139] = getOpcodeAlt(a);}
        op[140] = getOpcodeRnm(31, 144); // singleton
        op[141] = getOpcodeRnm(37, 176); // class
        op[142] = getOpcodeRnm(46, 206); // instance
        op[143] = getOpcodeRnm(47, 214); // element
        {int[] a = {145,146,147,148,149}; op[144] = getOpcodeAlt(a);}
        op[145] = getOpcodeRnm(32, 150); // entity-singleton
        op[146] = getOpcodeRnm(33, 154); // attribute-singleton
        op[147] = getOpcodeRnm(34, 158); // person-singleton
        op[148] = getOpcodeRnm(35, 163); // organization-singleton
        op[149] = getOpcodeRnm(36, 168); // relative-singleton
        {int[] a = {151,152,153}; op[150] = getOpcodeCat(a);}
        {char[] a = {33,40}; op[151] = getOpcodeTls(a);}
        op[152] = getOpcodeRnm(39, 182); // type-class
        {char[] a = {41}; op[153] = getOpcodeTls(a);}
        {int[] a = {155,156,157}; op[154] = getOpcodeCat(a);}
        {char[] a = {33,60}; op[155] = getOpcodeTls(a);}
        op[156] = getOpcodeRnm(39, 182); // type-class
        {char[] a = {62}; op[157] = getOpcodeTls(a);}
        {int[] a = {159,160}; op[158] = getOpcodeCat(a);}
        {char[] a = {61}; op[159] = getOpcodeTls(a);}
        {int[] a = {161,162}; op[160] = getOpcodeAlt(a);}
        op[161] = getOpcodeRnm(27, 118); // xref
        op[162] = getOpcodeRnm(62, 278); // xdi-chars
        {int[] a = {164,165}; op[163] = getOpcodeCat(a);}
        {char[] a = {64}; op[164] = getOpcodeTls(a);}
        {int[] a = {166,167}; op[165] = getOpcodeAlt(a);}
        op[166] = getOpcodeRnm(27, 118); // xref
        op[167] = getOpcodeRnm(62, 278); // xdi-chars
        {int[] a = {169,170}; op[168] = getOpcodeCat(a);}
        {char[] a = {42}; op[169] = getOpcodeTls(a);}
        op[170] = getOpcodeRep((char)0, (char)1, 171);
        {int[] a = {172,173,174,175}; op[171] = getOpcodeAlt(a);}
        op[172] = getOpcodeRnm(27, 118); // xref
        op[173] = getOpcodeRnm(55, 254); // uuid-literal
        op[174] = getOpcodeRnm(54, 245); // ipv6-literal
        op[175] = getOpcodeRnm(62, 278); // xdi-chars
        {int[] a = {177,178}; op[176] = getOpcodeAlt(a);}
        op[177] = getOpcodeRnm(38, 179); // entity-class
        op[178] = getOpcodeRnm(45, 202); // attribute-class
        {int[] a = {180,181}; op[179] = getOpcodeAlt(a);}
        op[180] = getOpcodeRnm(39, 182); // type-class
        op[181] = getOpcodeRnm(40, 185); // instance-class
        {int[] a = {183,184}; op[182] = getOpcodeAlt(a);}
        op[183] = getOpcodeRnm(41, 188); // specific
        op[184] = getOpcodeRnm(42, 194); // generic
        {int[] a = {186,187}; op[185] = getOpcodeAlt(a);}
        op[186] = getOpcodeRnm(43, 200); // person
        op[187] = getOpcodeRnm(44, 201); // organization
        {int[] a = {189,190}; op[188] = getOpcodeCat(a);}
        {char[] a = {36}; op[189] = getOpcodeTls(a);}
        op[190] = getOpcodeRep((char)0, (char)1, 191);
        {int[] a = {192,193}; op[191] = getOpcodeAlt(a);}
        op[192] = getOpcodeRnm(27, 118); // xref
        op[193] = getOpcodeRnm(62, 278); // xdi-chars
        {int[] a = {195,196}; op[194] = getOpcodeCat(a);}
        {char[] a = {43}; op[195] = getOpcodeTls(a);}
        op[196] = getOpcodeRep((char)0, (char)1, 197);
        {int[] a = {198,199}; op[197] = getOpcodeAlt(a);}
        op[198] = getOpcodeRnm(27, 118); // xref
        op[199] = getOpcodeRnm(62, 278); // xdi-chars
        {char[] a = {61}; op[200] = getOpcodeTls(a);}
        {char[] a = {64}; op[201] = getOpcodeTls(a);}
        {int[] a = {203,204,205}; op[202] = getOpcodeCat(a);}
        {char[] a = {60}; op[203] = getOpcodeTls(a);}
        op[204] = getOpcodeRnm(39, 182); // type-class
        {char[] a = {62}; op[205] = getOpcodeTls(a);}
        {int[] a = {207,208}; op[206] = getOpcodeCat(a);}
        {char[] a = {33}; op[207] = getOpcodeTls(a);}
        op[208] = getOpcodeRep((char)0, (char)1, 209);
        {int[] a = {210,211,212,213}; op[209] = getOpcodeAlt(a);}
        op[210] = getOpcodeRnm(27, 118); // xref
        op[211] = getOpcodeRnm(55, 254); // uuid-literal
        op[212] = getOpcodeRnm(54, 245); // ipv6-literal
        op[213] = getOpcodeRnm(62, 278); // xdi-chars
        {int[] a = {215,216,218}; op[214] = getOpcodeCat(a);}
        {char[] a = {91}; op[215] = getOpcodeTls(a);}
        op[216] = getOpcodeRep((char)1, Character.MAX_VALUE, 217);
        op[217] = getOpcodeRnm(68, 312); // DIGIT
        {char[] a = {93}; op[218] = getOpcodeTls(a);}
        {int[] a = {220,221,222,223,224}; op[219] = getOpcodeAlt(a);}
        op[220] = getOpcodeRnm(49, 225); // json-string
        op[221] = getOpcodeRnm(50, 230); // json-number
        op[222] = getOpcodeRnm(51, 232); // json-boolean
        op[223] = getOpcodeRnm(52, 235); // json-array
        op[224] = getOpcodeRnm(53, 240); // json-object
        {int[] a = {226,227,229}; op[225] = getOpcodeCat(a);}
        op[226] = getOpcodeRnm(71, 322); // DQUOTE
        op[227] = getOpcodeRep((char)0, Character.MAX_VALUE, 228);
        op[228] = getOpcodeRnm(62, 278); // xdi-chars
        op[229] = getOpcodeRnm(71, 322); // DQUOTE
        op[230] = getOpcodeRep((char)1, Character.MAX_VALUE, 231);
        op[231] = getOpcodeRnm(68, 312); // DIGIT
        {int[] a = {233,234}; op[232] = getOpcodeAlt(a);}
        {char[] a = {116,114,117,101}; op[233] = getOpcodeTls(a);}
        {char[] a = {102,97,108,115,101}; op[234] = getOpcodeTls(a);}
        {int[] a = {236,237,239}; op[235] = getOpcodeCat(a);}
        {char[] a = {91}; op[236] = getOpcodeTls(a);}
        op[237] = getOpcodeRep((char)0, Character.MAX_VALUE, 238);
        op[238] = getOpcodeRnm(66, 301); // xdi-char
        {char[] a = {93}; op[239] = getOpcodeTls(a);}
        {int[] a = {241,242,244}; op[240] = getOpcodeCat(a);}
        {char[] a = {123}; op[241] = getOpcodeTls(a);}
        op[242] = getOpcodeRep((char)0, Character.MAX_VALUE, 243);
        op[243] = getOpcodeRnm(66, 301); // xdi-char
        {char[] a = {125}; op[244] = getOpcodeTls(a);}
        {int[] a = {246,247,249}; op[245] = getOpcodeCat(a);}
        {char[] a = {58,105,112,118,54,58}; op[246] = getOpcodeTls(a);}
        op[247] = getOpcodeRep((char)4, (char)4, 248);
        op[248] = getOpcodeRnm(69, 313); // HEXDIG
        op[249] = getOpcodeRep((char)7, Character.MAX_VALUE, 250);
        {int[] a = {251,252}; op[250] = getOpcodeCat(a);}
        {char[] a = {58}; op[251] = getOpcodeTls(a);}
        op[252] = getOpcodeRep((char)4, (char)4, 253);
        op[253] = getOpcodeRnm(69, 313); // HEXDIG
        {int[] a = {255,256,257,258,259,260,261,262,263,264,265}; op[254] = getOpcodeCat(a);}
        {char[] a = {58,117,117,105,100,58}; op[255] = getOpcodeTls(a);}
        op[256] = getOpcodeRnm(56, 266); // time-low
        {char[] a = {45}; op[257] = getOpcodeTls(a);}
        op[258] = getOpcodeRnm(57, 268); // time-mid
        {char[] a = {45}; op[259] = getOpcodeTls(a);}
        op[260] = getOpcodeRnm(58, 270); // time-high
        {char[] a = {45}; op[261] = getOpcodeTls(a);}
        op[262] = getOpcodeRnm(59, 272); // clock-seq
        op[263] = getOpcodeRnm(60, 274); // clock-seq-low
        {char[] a = {45}; op[264] = getOpcodeTls(a);}
        op[265] = getOpcodeRnm(61, 276); // node
        op[266] = getOpcodeRep((char)8, (char)8, 267);
        op[267] = getOpcodeRnm(69, 313); // HEXDIG
        op[268] = getOpcodeRep((char)4, (char)4, 269);
        op[269] = getOpcodeRnm(69, 313); // HEXDIG
        op[270] = getOpcodeRep((char)4, (char)4, 271);
        op[271] = getOpcodeRnm(69, 313); // HEXDIG
        op[272] = getOpcodeRep((char)2, (char)2, 273);
        op[273] = getOpcodeRnm(69, 313); // HEXDIG
        op[274] = getOpcodeRep((char)2, (char)2, 275);
        op[275] = getOpcodeRnm(69, 313); // HEXDIG
        op[276] = getOpcodeRep((char)12, (char)12, 277);
        op[277] = getOpcodeRnm(69, 313); // HEXDIG
        op[278] = getOpcodeRep((char)1, Character.MAX_VALUE, 279);
        {int[] a = {280,281}; op[279] = getOpcodeAlt(a);}
        op[280] = getOpcodeRnm(66, 301); // xdi-char
        {char[] a = {58}; op[281] = getOpcodeTls(a);}
        {int[] a = {283,284,285}; op[282] = getOpcodeAlt(a);}
        op[283] = getOpcodeRnm(66, 301); // xdi-char
        op[284] = getOpcodeRnm(65, 294); // context-symbol
        op[285] = getOpcodeRnm(64, 286); // nonparen-delim
        {int[] a = {287,288,289,290,291,292,293}; op[286] = getOpcodeAlt(a);}
        {char[] a = {47}; op[287] = getOpcodeTls(a);}
        {char[] a = {63}; op[288] = getOpcodeTls(a);}
        {char[] a = {35}; op[289] = getOpcodeTls(a);}
        {char[] a = {91}; op[290] = getOpcodeTls(a);}
        {char[] a = {93}; op[291] = getOpcodeTls(a);}
        {char[] a = {39}; op[292] = getOpcodeTls(a);}
        {char[] a = {44}; op[293] = getOpcodeTls(a);}
        {int[] a = {295,296,297,298,299,300}; op[294] = getOpcodeAlt(a);}
        {char[] a = {33}; op[295] = getOpcodeTls(a);}
        {char[] a = {42}; op[296] = getOpcodeTls(a);}
        {char[] a = {61}; op[297] = getOpcodeTls(a);}
        {char[] a = {64}; op[298] = getOpcodeTls(a);}
        {char[] a = {43}; op[299] = getOpcodeTls(a);}
        {char[] a = {36}; op[300] = getOpcodeTls(a);}
        {int[] a = {302,303,304,305,306,307,308}; op[301] = getOpcodeAlt(a);}
        op[302] = getOpcodeRnm(67, 309); // ALPHA
        op[303] = getOpcodeRnm(68, 312); // DIGIT
        {char[] a = {45}; op[304] = getOpcodeTls(a);}
        {char[] a = {46}; op[305] = getOpcodeTls(a);}
        {char[] a = {95}; op[306] = getOpcodeTls(a);}
        {char[] a = {126}; op[307] = getOpcodeTls(a);}
        op[308] = getOpcodeTrg((char)128, (char)65533);
        {int[] a = {310,311}; op[309] = getOpcodeAlt(a);}
        op[310] = getOpcodeTrg((char)65, (char)90);
        op[311] = getOpcodeTrg((char)97, (char)122);
        op[312] = getOpcodeTrg((char)48, (char)57);
        {int[] a = {314,315}; op[313] = getOpcodeAlt(a);}
        op[314] = getOpcodeTrg((char)48, (char)57);
        op[315] = getOpcodeTrg((char)65, (char)70);
        {int[] a = {317,318,319}; op[316] = getOpcodeAlt(a);}
        {char[] a = {13}; op[317] = getOpcodeTbs(a);}
        {char[] a = {10}; op[318] = getOpcodeTbs(a);}
        {int[] a = {320,321}; op[319] = getOpcodeCat(a);}
        {char[] a = {13}; op[320] = getOpcodeTbs(a);}
        {char[] a = {10}; op[321] = getOpcodeTbs(a);}
        {char[] a = {34}; op[322] = getOpcodeTbs(a);}
        return op;
    }

    public static void display(PrintStream out){
        out.println(";");
        out.println("; xdi2.core.xri3.parser.apg.XDI3Grammar");
        out.println(";");
        out.println("xdi-graph               = xdi-statement *( CRLF xdi-statement )");
        out.println("xdi-statement           = contextual-statement / relational-statement / literal-statement");
        out.println("");
        out.println("contextual-statement    = direct-contextual / inverse-contextual");
        out.println("direct-contextual       = absolute / peer-relative / context-relative / class-relative");
        out.println("inverse-contextual      = absolute-inverse / peer-inverse  / context-inverse / class-inverse");
        out.println("");
        out.println("absolute                =                   \"()/()/\" context");
        out.println("peer-relative           =               peer  \"/()/\" context");
        out.println("context-relative        =            context  \"/()/\" relative-context");
        out.println("class-relative          =       class-context \"/()/\" instance-context");
        out.println("absolute-inverse        =          context \"/$is()/()\"");
        out.println("peer-inverse            =          context \"/$is()/\" peer");
        out.println("context-inverse         = relative-context \"/$is()/\" context");
        out.println("class-inverse           = instance-context \"/$is()/\" class-context");
        out.println("");
        out.println("relational-statement    = inverse-relational / direct-relational");
        out.println("direct-relational       = context \"/\"    context \"/\" context");
        out.println("inverse-relational      = context \"/$is\" context \"/\" context");
        out.println("");
        out.println("literal-statement       = literal-context value-context \"/<>/\" json-value");
        out.println("");
        out.println("context                 = ( peer [ relative-context ] ) / relative-context");
        out.println("relative-context        = 1*subpath");
        out.println("");
        out.println("class-context           = [ peer ] class-path");
        out.println("class-path              = ( subpath class-path ) / class");
        out.println("");
        out.println("instance-context        = ( instance / element ) [ relative-context ]");
        out.println("");
        out.println("literal-context         = [ peer ] literal-path");
        out.println("literal-path            = ( subpath literal-path ) / attribute-singleton / attribute-pair");
        out.println("attribute-pair          = attribute-class ( instance / element )");
        out.println("");
        out.println("value-context           = \"<<\" 1*type-class \">>\"");
        out.println("");
        out.println("peer                    = 1*xref");
        out.println("xref                    = \"(\" ( context / inner-root / xdi-statement / iri-chars / xdi-chars ) \")\"");
        out.println("inner-root              = context \"/\" context");
        out.println("");
        out.println("subpath                 = singleton / ( class [ instance / element ] )");
        out.println("subsegment              = singleton / class / instance / element");
        out.println("");
        out.println("singleton               = entity-singleton / attribute-singleton / person-singleton / organization-singleton / relative-singleton");
        out.println("entity-singleton        = \"!(\" type-class \")\"");
        out.println("attribute-singleton     = \"!<\" type-class \">\"");
        out.println("person-singleton        = \"=\" ( xref / xdi-chars )");
        out.println("organization-singleton  = \"@\" ( xref / xdi-chars )");
        out.println("relative-singleton      = \"*\" [ xref / uuid-literal / ipv6-literal / xdi-chars ]");
        out.println("");
        out.println("class                   = entity-class / attribute-class");
        out.println("");
        out.println("entity-class            = type-class / instance-class");
        out.println("type-class              = specific / generic");
        out.println("instance-class          = person / organization");
        out.println("specific                = \"$\" [ xref / xdi-chars ]");
        out.println("generic                 = \"+\" [ xref / xdi-chars ]");
        out.println("person                  = \"=\"");
        out.println("organization            = \"@\"");
        out.println("");
        out.println("attribute-class         = \"<\" type-class \">\"");
        out.println("");
        out.println("instance                = \"!\" [ xref / uuid-literal / ipv6-literal / xdi-chars ]");
        out.println("");
        out.println("element                 = \"[\" 1*DIGIT \"]\"");
        out.println("");
        out.println("json-value              = json-string / json-number / json-boolean / json-array / json-object");
        out.println("json-string             = DQUOTE *xdi-chars DQUOTE   ;needs real JSON ABNF");
        out.println("json-number             = 1*DIGIT                    ;needs real JSON ABNF");
        out.println("json-boolean            = \"true\" / \"false\"");
        out.println("json-array              = \"[\" *xdi-char \"]\"          ;needs real JSON ABNF");
        out.println("json-object             = \"{\" *xdi-char \"}\"          ;needs real JSON ABNF");
        out.println("");
        out.println("ipv6-literal            = \":ipv6:\" 4HEXDIG 7*( \":\" 4HEXDIG )");
        out.println("");
        out.println("uuid-literal            = \":uuid:\" time-low \"-\" time-mid \"-\" time-high \"-\" clock-seq clock-seq-low \"-\" node");
        out.println("time-low                = 8HEXDIG");
        out.println("time-mid                = 4HEXDIG");
        out.println("time-high               = 4HEXDIG   ; includes version");
        out.println("clock-seq               = 2HEXDIG   ; includes reserved");
        out.println("clock-seq-low           = 2HEXDIG");
        out.println("node                    = 12HEXDIG");
        out.println("");
        out.println("xdi-chars               = 1*( xdi-char / \":\" )");
        out.println("");
        out.println("iri-chars               = xdi-char / context-symbol / nonparen-delim ; \"(\" and \")\" are excluded");
        out.println("nonparen-delim          = \"/\" / \"?\" / \"#\" / \"[\" / \"]\" / \"'\" / \",\"    ; double quote included?");
        out.println("context-symbol          = \"!\" / \"*\" / \"=\" / \"@\" / \"+\" / \"$\"");
        out.println("xdi-char                = ALPHA / DIGIT / \"-\" / \".\" / \"_\" / \"~\" / %x80-EFFFD ; is there a way to leave high end open?");
        out.println("ALPHA                   = %x41-5A / %x61-7A   ; A-Z, a-z");
        out.println("DIGIT                   = %x30-39             ; 0-9");
        out.println("HEXDIG                  = %x30-39 / %x41-46   ; 0-9, A-F");
        out.println("CRLF                    = %x0D / %x0A / ( %x0D %x0A )");
        out.println("DQUOTE                  = %x22");
    }
}
