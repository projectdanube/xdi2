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
    public static int ruleCount = 76;
    public enum RuleNames{
        ABSOLUTE("absolute", 5, 23, 4),
        ABSOLUTE_INVERSE("absolute-inverse", 9, 39, 4),
        ALPHA("ALPHA", 71, 333, 3),
        ATTRIBUTE_CLASS("attribute-class", 49, 226, 4),
        ATTRIBUTE_PAIR("attribute-pair", 26, 119, 5),
        ATTRIBUTE_SINGLETON("attribute-singleton", 37, 178, 4),
        CLASS("class", 41, 200, 3),
        CLASS_CONTEXT("class-context", 21, 94, 4),
        CLASS_INVERSE("class-inverse", 12, 51, 4),
        CLASS_PATH("class-path", 22, 98, 5),
        CLASS_RELATIVE("class-relative", 8, 35, 4),
        CLOCK_SEQ("clock-seq", 63, 296, 2),
        CLOCK_SEQ_LOW("clock-seq-low", 64, 298, 2),
        CONTEXT("context", 19, 86, 6),
        CONTEXT_INVERSE("context-inverse", 11, 47, 4),
        CONTEXT_RELATIVE("context-relative", 7, 31, 4),
        CONTEXT_SYMBOL("context-symbol", 69, 318, 7),
        CONTEXTUAL_STATEMENT("contextual-statement", 2, 10, 3),
        CRLF("CRLF", 74, 340, 6),
        DIGIT("DIGIT", 72, 336, 1),
        DIRECT_CONTEXTUAL("direct-contextual", 3, 13, 5),
        DIRECT_RELATIONAL("direct-relational", 14, 59, 6),
        DQUOTE("DQUOTE", 75, 346, 1),
        ELEMENT("element", 51, 238, 5),
        ENTITY_CLASS("entity-class", 42, 203, 3),
        ENTITY_SINGLETON("entity-singleton", 36, 174, 4),
        GENERIC("generic", 46, 218, 6),
        HEXDIG("HEXDIG", 73, 337, 3),
        INNER_RELATIONAL("inner-relational", 16, 71, 6),
        INNER_ROOT("inner-root", 31, 140, 6),
        INNER_STATEMENT("inner-statement", 17, 77, 4),
        INSTANCE("instance", 50, 230, 8),
        INSTANCE_CLASS("instance-class", 44, 209, 3),
        INSTANCE_CONTEXT("instance-context", 23, 103, 6),
        INVERSE_CONTEXTUAL("inverse-contextual", 4, 18, 5),
        INVERSE_RELATIONAL("inverse-relational", 15, 65, 6),
        IPV6_LITERAL("ipv6-literal", 58, 269, 9),
        IRI_CHARS("iri-chars", 67, 306, 4),
        JSON_ARRAY("json-array", 56, 259, 5),
        JSON_BOOLEAN("json-boolean", 55, 256, 3),
        JSON_NUMBER("json-number", 54, 254, 2),
        JSON_OBJECT("json-object", 57, 264, 5),
        JSON_STRING("json-string", 53, 249, 5),
        JSON_VALUE("json-value", 52, 243, 6),
        LITERAL_CONTEXT("literal-context", 24, 109, 4),
        LITERAL_PATH("literal-path", 25, 113, 6),
        LITERAL_STATEMENT("literal-statement", 18, 81, 5),
        LOCAL_ROOT("local-root", 29, 133, 1),
        NODE("node", 65, 300, 2),
        NONPAREN_DELIM("nonparen-delim", 68, 310, 8),
        ORGANIZATION("organization", 48, 225, 1),
        ORGANIZATION_SINGLETON("organization-singleton", 39, 187, 5),
        PEER_INVERSE("peer-inverse", 10, 43, 4),
        PEER_RELATIVE("peer-relative", 6, 27, 4),
        PEER_ROOT("peer-root", 30, 134, 6),
        PERSON("person", 47, 224, 1),
        PERSON_SINGLETON("person-singleton", 38, 182, 5),
        RELATIONAL_STATEMENT("relational-statement", 13, 55, 4),
        RELATIVE_CONTEXT("relative-context", 20, 92, 2),
        RELATIVE_SINGLETON("relative-singleton", 40, 192, 8),
        ROOT("root", 28, 129, 4),
        SINGLETON("singleton", 35, 168, 6),
        SPECIFIC("specific", 45, 212, 6),
        SUBPATH("subpath", 33, 153, 8),
        SUBSEGMENT("subsegment", 34, 161, 7),
        TIME_HIGH("time-high", 62, 294, 2),
        TIME_LOW("time-low", 60, 290, 2),
        TIME_MID("time-mid", 61, 292, 2),
        TYPE_CLASS("type-class", 43, 206, 3),
        UUID_LITERAL("uuid-literal", 59, 278, 12),
        VALUE_CONTEXT("value-context", 27, 124, 5),
        XDI_CHAR("xdi-char", 70, 325, 8),
        XDI_CHARS("xdi-chars", 66, 302, 4),
        XDI_GRAPH("xdi-graph", 0, 0, 6),
        XDI_STATEMENT("xdi-statement", 1, 6, 4),
        XREF("xref", 32, 146, 7);
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
    	Rule[] rules = new Rule[76];
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
    	Opcode[] op = new Opcode[347];
        {int[] a = {1,2}; op[0] = getOpcodeCat(a);}
        op[1] = getOpcodeRnm(1, 6); // xdi-statement
        op[2] = getOpcodeRep((char)0, Character.MAX_VALUE, 3);
        {int[] a = {4,5}; op[3] = getOpcodeCat(a);}
        op[4] = getOpcodeRnm(74, 340); // CRLF
        op[5] = getOpcodeRnm(1, 6); // xdi-statement
        {int[] a = {7,8,9}; op[6] = getOpcodeAlt(a);}
        op[7] = getOpcodeRnm(2, 10); // contextual-statement
        op[8] = getOpcodeRnm(13, 55); // relational-statement
        op[9] = getOpcodeRnm(18, 81); // literal-statement
        {int[] a = {11,12}; op[10] = getOpcodeAlt(a);}
        op[11] = getOpcodeRnm(3, 13); // direct-contextual
        op[12] = getOpcodeRnm(4, 18); // inverse-contextual
        {int[] a = {14,15,16,17}; op[13] = getOpcodeAlt(a);}
        op[14] = getOpcodeRnm(5, 23); // absolute
        op[15] = getOpcodeRnm(6, 27); // peer-relative
        op[16] = getOpcodeRnm(7, 31); // context-relative
        op[17] = getOpcodeRnm(8, 35); // class-relative
        {int[] a = {19,20,21,22}; op[18] = getOpcodeAlt(a);}
        op[19] = getOpcodeRnm(9, 39); // absolute-inverse
        op[20] = getOpcodeRnm(10, 43); // peer-inverse
        op[21] = getOpcodeRnm(11, 47); // context-inverse
        op[22] = getOpcodeRnm(12, 51); // class-inverse
        {int[] a = {24,25,26}; op[23] = getOpcodeCat(a);}
        op[24] = getOpcodeRnm(29, 133); // local-root
        {char[] a = {47,40,41,47}; op[25] = getOpcodeTls(a);}
        op[26] = getOpcodeRnm(19, 86); // context
        {int[] a = {28,29,30}; op[27] = getOpcodeCat(a);}
        op[28] = getOpcodeRnm(30, 134); // peer-root
        {char[] a = {47,40,41,47}; op[29] = getOpcodeTls(a);}
        op[30] = getOpcodeRnm(19, 86); // context
        {int[] a = {32,33,34}; op[31] = getOpcodeCat(a);}
        op[32] = getOpcodeRnm(19, 86); // context
        {char[] a = {47,40,41,47}; op[33] = getOpcodeTls(a);}
        op[34] = getOpcodeRnm(20, 92); // relative-context
        {int[] a = {36,37,38}; op[35] = getOpcodeCat(a);}
        op[36] = getOpcodeRnm(21, 94); // class-context
        {char[] a = {47,40,41,47}; op[37] = getOpcodeTls(a);}
        op[38] = getOpcodeRnm(23, 103); // instance-context
        {int[] a = {40,41,42}; op[39] = getOpcodeCat(a);}
        op[40] = getOpcodeRnm(19, 86); // context
        {char[] a = {47,36,105,115,40,41,47}; op[41] = getOpcodeTls(a);}
        op[42] = getOpcodeRnm(29, 133); // local-root
        {int[] a = {44,45,46}; op[43] = getOpcodeCat(a);}
        op[44] = getOpcodeRnm(19, 86); // context
        {char[] a = {47,36,105,115,40,41,47}; op[45] = getOpcodeTls(a);}
        op[46] = getOpcodeRnm(30, 134); // peer-root
        {int[] a = {48,49,50}; op[47] = getOpcodeCat(a);}
        op[48] = getOpcodeRnm(20, 92); // relative-context
        {char[] a = {47,36,105,115,40,41,47}; op[49] = getOpcodeTls(a);}
        op[50] = getOpcodeRnm(19, 86); // context
        {int[] a = {52,53,54}; op[51] = getOpcodeCat(a);}
        op[52] = getOpcodeRnm(23, 103); // instance-context
        {char[] a = {47,36,105,115,40,41,47}; op[53] = getOpcodeTls(a);}
        op[54] = getOpcodeRnm(21, 94); // class-context
        {int[] a = {56,57,58}; op[55] = getOpcodeAlt(a);}
        op[56] = getOpcodeRnm(15, 65); // inverse-relational
        op[57] = getOpcodeRnm(14, 59); // direct-relational
        op[58] = getOpcodeRnm(16, 71); // inner-relational
        {int[] a = {60,61,62,63,64}; op[59] = getOpcodeCat(a);}
        op[60] = getOpcodeRnm(19, 86); // context
        {char[] a = {47}; op[61] = getOpcodeTls(a);}
        op[62] = getOpcodeRnm(19, 86); // context
        {char[] a = {47}; op[63] = getOpcodeTls(a);}
        op[64] = getOpcodeRnm(19, 86); // context
        {int[] a = {66,67,68,69,70}; op[65] = getOpcodeCat(a);}
        op[66] = getOpcodeRnm(19, 86); // context
        {char[] a = {47,36,105,115}; op[67] = getOpcodeTls(a);}
        op[68] = getOpcodeRnm(19, 86); // context
        {char[] a = {47}; op[69] = getOpcodeTls(a);}
        op[70] = getOpcodeRnm(19, 86); // context
        {int[] a = {72,73,74,75,76}; op[71] = getOpcodeCat(a);}
        op[72] = getOpcodeRnm(19, 86); // context
        {char[] a = {47}; op[73] = getOpcodeTls(a);}
        op[74] = getOpcodeRnm(19, 86); // context
        {char[] a = {47}; op[75] = getOpcodeTls(a);}
        op[76] = getOpcodeRnm(17, 77); // inner-statement
        {int[] a = {78,79,80}; op[77] = getOpcodeCat(a);}
        {char[] a = {40}; op[78] = getOpcodeTls(a);}
        op[79] = getOpcodeRnm(1, 6); // xdi-statement
        {char[] a = {41}; op[80] = getOpcodeTls(a);}
        {int[] a = {82,83,84,85}; op[81] = getOpcodeCat(a);}
        op[82] = getOpcodeRnm(24, 109); // literal-context
        op[83] = getOpcodeRnm(27, 124); // value-context
        {char[] a = {47,60,62,47}; op[84] = getOpcodeTls(a);}
        op[85] = getOpcodeRnm(52, 243); // json-value
        {int[] a = {87,91}; op[86] = getOpcodeAlt(a);}
        {int[] a = {88,89}; op[87] = getOpcodeCat(a);}
        op[88] = getOpcodeRnm(30, 134); // peer-root
        op[89] = getOpcodeRep((char)0, (char)1, 90);
        op[90] = getOpcodeRnm(20, 92); // relative-context
        op[91] = getOpcodeRnm(20, 92); // relative-context
        op[92] = getOpcodeRep((char)1, Character.MAX_VALUE, 93);
        op[93] = getOpcodeRnm(33, 153); // subpath
        {int[] a = {95,97}; op[94] = getOpcodeCat(a);}
        op[95] = getOpcodeRep((char)0, (char)1, 96);
        op[96] = getOpcodeRnm(30, 134); // peer-root
        op[97] = getOpcodeRnm(22, 98); // class-path
        {int[] a = {99,102}; op[98] = getOpcodeAlt(a);}
        {int[] a = {100,101}; op[99] = getOpcodeCat(a);}
        op[100] = getOpcodeRnm(33, 153); // subpath
        op[101] = getOpcodeRnm(22, 98); // class-path
        op[102] = getOpcodeRnm(41, 200); // class
        {int[] a = {104,107}; op[103] = getOpcodeCat(a);}
        {int[] a = {105,106}; op[104] = getOpcodeAlt(a);}
        op[105] = getOpcodeRnm(50, 230); // instance
        op[106] = getOpcodeRnm(51, 238); // element
        op[107] = getOpcodeRep((char)0, (char)1, 108);
        op[108] = getOpcodeRnm(20, 92); // relative-context
        {int[] a = {110,112}; op[109] = getOpcodeCat(a);}
        op[110] = getOpcodeRep((char)0, (char)1, 111);
        op[111] = getOpcodeRnm(30, 134); // peer-root
        op[112] = getOpcodeRnm(25, 113); // literal-path
        {int[] a = {114,117,118}; op[113] = getOpcodeAlt(a);}
        {int[] a = {115,116}; op[114] = getOpcodeCat(a);}
        op[115] = getOpcodeRnm(33, 153); // subpath
        op[116] = getOpcodeRnm(25, 113); // literal-path
        op[117] = getOpcodeRnm(37, 178); // attribute-singleton
        op[118] = getOpcodeRnm(26, 119); // attribute-pair
        {int[] a = {120,121}; op[119] = getOpcodeCat(a);}
        op[120] = getOpcodeRnm(49, 226); // attribute-class
        {int[] a = {122,123}; op[121] = getOpcodeAlt(a);}
        op[122] = getOpcodeRnm(50, 230); // instance
        op[123] = getOpcodeRnm(51, 238); // element
        {int[] a = {125,126,128}; op[124] = getOpcodeCat(a);}
        {char[] a = {60,60}; op[125] = getOpcodeTls(a);}
        op[126] = getOpcodeRep((char)1, Character.MAX_VALUE, 127);
        op[127] = getOpcodeRnm(43, 206); // type-class
        {char[] a = {62,62}; op[128] = getOpcodeTls(a);}
        {int[] a = {130,131,132}; op[129] = getOpcodeAlt(a);}
        op[130] = getOpcodeRnm(29, 133); // local-root
        op[131] = getOpcodeRnm(30, 134); // peer-root
        op[132] = getOpcodeRnm(31, 140); // inner-root
        {char[] a = {40,41}; op[133] = getOpcodeTls(a);}
        {int[] a = {135,136,139}; op[134] = getOpcodeCat(a);}
        {char[] a = {40}; op[135] = getOpcodeTls(a);}
        {int[] a = {137,138}; op[136] = getOpcodeAlt(a);}
        op[137] = getOpcodeRnm(19, 86); // context
        op[138] = getOpcodeRnm(67, 306); // iri-chars
        {char[] a = {41}; op[139] = getOpcodeTls(a);}
        {int[] a = {141,142,143,144,145}; op[140] = getOpcodeCat(a);}
        {char[] a = {40}; op[141] = getOpcodeTls(a);}
        op[142] = getOpcodeRnm(19, 86); // context
        {char[] a = {47}; op[143] = getOpcodeTls(a);}
        op[144] = getOpcodeRnm(19, 86); // context
        {char[] a = {41}; op[145] = getOpcodeTls(a);}
        {int[] a = {147,148,152}; op[146] = getOpcodeCat(a);}
        {char[] a = {40}; op[147] = getOpcodeTls(a);}
        {int[] a = {149,150,151}; op[148] = getOpcodeAlt(a);}
        op[149] = getOpcodeRnm(19, 86); // context
        op[150] = getOpcodeRnm(67, 306); // iri-chars
        op[151] = getOpcodeRnm(66, 302); // xdi-chars
        {char[] a = {41}; op[152] = getOpcodeTls(a);}
        {int[] a = {154,155}; op[153] = getOpcodeAlt(a);}
        op[154] = getOpcodeRnm(35, 168); // singleton
        {int[] a = {156,157}; op[155] = getOpcodeCat(a);}
        op[156] = getOpcodeRnm(41, 200); // class
        op[157] = getOpcodeRep((char)0, (char)1, 158);
        {int[] a = {159,160}; op[158] = getOpcodeAlt(a);}
        op[159] = getOpcodeRnm(50, 230); // instance
        op[160] = getOpcodeRnm(51, 238); // element
        {int[] a = {162,163,164,165,166,167}; op[161] = getOpcodeAlt(a);}
        op[162] = getOpcodeRnm(35, 168); // singleton
        op[163] = getOpcodeRnm(41, 200); // class
        op[164] = getOpcodeRnm(50, 230); // instance
        op[165] = getOpcodeRnm(51, 238); // element
        op[166] = getOpcodeRnm(28, 129); // root
        op[167] = getOpcodeRnm(17, 77); // inner-statement
        {int[] a = {169,170,171,172,173}; op[168] = getOpcodeAlt(a);}
        op[169] = getOpcodeRnm(36, 174); // entity-singleton
        op[170] = getOpcodeRnm(37, 178); // attribute-singleton
        op[171] = getOpcodeRnm(38, 182); // person-singleton
        op[172] = getOpcodeRnm(39, 187); // organization-singleton
        op[173] = getOpcodeRnm(40, 192); // relative-singleton
        {int[] a = {175,176,177}; op[174] = getOpcodeCat(a);}
        {char[] a = {33,40}; op[175] = getOpcodeTls(a);}
        op[176] = getOpcodeRnm(43, 206); // type-class
        {char[] a = {41}; op[177] = getOpcodeTls(a);}
        {int[] a = {179,180,181}; op[178] = getOpcodeCat(a);}
        {char[] a = {33,60}; op[179] = getOpcodeTls(a);}
        op[180] = getOpcodeRnm(43, 206); // type-class
        {char[] a = {62}; op[181] = getOpcodeTls(a);}
        {int[] a = {183,184}; op[182] = getOpcodeCat(a);}
        {char[] a = {61}; op[183] = getOpcodeTls(a);}
        {int[] a = {185,186}; op[184] = getOpcodeAlt(a);}
        op[185] = getOpcodeRnm(32, 146); // xref
        op[186] = getOpcodeRnm(66, 302); // xdi-chars
        {int[] a = {188,189}; op[187] = getOpcodeCat(a);}
        {char[] a = {64}; op[188] = getOpcodeTls(a);}
        {int[] a = {190,191}; op[189] = getOpcodeAlt(a);}
        op[190] = getOpcodeRnm(32, 146); // xref
        op[191] = getOpcodeRnm(66, 302); // xdi-chars
        {int[] a = {193,194}; op[192] = getOpcodeCat(a);}
        {char[] a = {42}; op[193] = getOpcodeTls(a);}
        op[194] = getOpcodeRep((char)0, (char)1, 195);
        {int[] a = {196,197,198,199}; op[195] = getOpcodeAlt(a);}
        op[196] = getOpcodeRnm(32, 146); // xref
        op[197] = getOpcodeRnm(59, 278); // uuid-literal
        op[198] = getOpcodeRnm(58, 269); // ipv6-literal
        op[199] = getOpcodeRnm(66, 302); // xdi-chars
        {int[] a = {201,202}; op[200] = getOpcodeAlt(a);}
        op[201] = getOpcodeRnm(42, 203); // entity-class
        op[202] = getOpcodeRnm(49, 226); // attribute-class
        {int[] a = {204,205}; op[203] = getOpcodeAlt(a);}
        op[204] = getOpcodeRnm(43, 206); // type-class
        op[205] = getOpcodeRnm(44, 209); // instance-class
        {int[] a = {207,208}; op[206] = getOpcodeAlt(a);}
        op[207] = getOpcodeRnm(45, 212); // specific
        op[208] = getOpcodeRnm(46, 218); // generic
        {int[] a = {210,211}; op[209] = getOpcodeAlt(a);}
        op[210] = getOpcodeRnm(47, 224); // person
        op[211] = getOpcodeRnm(48, 225); // organization
        {int[] a = {213,214}; op[212] = getOpcodeCat(a);}
        {char[] a = {36}; op[213] = getOpcodeTls(a);}
        op[214] = getOpcodeRep((char)0, (char)1, 215);
        {int[] a = {216,217}; op[215] = getOpcodeAlt(a);}
        op[216] = getOpcodeRnm(32, 146); // xref
        op[217] = getOpcodeRnm(66, 302); // xdi-chars
        {int[] a = {219,220}; op[218] = getOpcodeCat(a);}
        {char[] a = {43}; op[219] = getOpcodeTls(a);}
        op[220] = getOpcodeRep((char)0, (char)1, 221);
        {int[] a = {222,223}; op[221] = getOpcodeAlt(a);}
        op[222] = getOpcodeRnm(32, 146); // xref
        op[223] = getOpcodeRnm(66, 302); // xdi-chars
        {char[] a = {61}; op[224] = getOpcodeTls(a);}
        {char[] a = {64}; op[225] = getOpcodeTls(a);}
        {int[] a = {227,228,229}; op[226] = getOpcodeCat(a);}
        {char[] a = {60}; op[227] = getOpcodeTls(a);}
        op[228] = getOpcodeRnm(43, 206); // type-class
        {char[] a = {62}; op[229] = getOpcodeTls(a);}
        {int[] a = {231,232}; op[230] = getOpcodeCat(a);}
        {char[] a = {33}; op[231] = getOpcodeTls(a);}
        op[232] = getOpcodeRep((char)0, (char)1, 233);
        {int[] a = {234,235,236,237}; op[233] = getOpcodeAlt(a);}
        op[234] = getOpcodeRnm(32, 146); // xref
        op[235] = getOpcodeRnm(59, 278); // uuid-literal
        op[236] = getOpcodeRnm(58, 269); // ipv6-literal
        op[237] = getOpcodeRnm(66, 302); // xdi-chars
        {int[] a = {239,240,242}; op[238] = getOpcodeCat(a);}
        {char[] a = {91}; op[239] = getOpcodeTls(a);}
        op[240] = getOpcodeRep((char)1, Character.MAX_VALUE, 241);
        op[241] = getOpcodeRnm(72, 336); // DIGIT
        {char[] a = {93}; op[242] = getOpcodeTls(a);}
        {int[] a = {244,245,246,247,248}; op[243] = getOpcodeAlt(a);}
        op[244] = getOpcodeRnm(53, 249); // json-string
        op[245] = getOpcodeRnm(54, 254); // json-number
        op[246] = getOpcodeRnm(55, 256); // json-boolean
        op[247] = getOpcodeRnm(56, 259); // json-array
        op[248] = getOpcodeRnm(57, 264); // json-object
        {int[] a = {250,251,253}; op[249] = getOpcodeCat(a);}
        op[250] = getOpcodeRnm(75, 346); // DQUOTE
        op[251] = getOpcodeRep((char)0, Character.MAX_VALUE, 252);
        op[252] = getOpcodeRnm(66, 302); // xdi-chars
        op[253] = getOpcodeRnm(75, 346); // DQUOTE
        op[254] = getOpcodeRep((char)1, Character.MAX_VALUE, 255);
        op[255] = getOpcodeRnm(72, 336); // DIGIT
        {int[] a = {257,258}; op[256] = getOpcodeAlt(a);}
        {char[] a = {116,114,117,101}; op[257] = getOpcodeTls(a);}
        {char[] a = {102,97,108,115,101}; op[258] = getOpcodeTls(a);}
        {int[] a = {260,261,263}; op[259] = getOpcodeCat(a);}
        {char[] a = {91}; op[260] = getOpcodeTls(a);}
        op[261] = getOpcodeRep((char)0, Character.MAX_VALUE, 262);
        op[262] = getOpcodeRnm(70, 325); // xdi-char
        {char[] a = {93}; op[263] = getOpcodeTls(a);}
        {int[] a = {265,266,268}; op[264] = getOpcodeCat(a);}
        {char[] a = {123}; op[265] = getOpcodeTls(a);}
        op[266] = getOpcodeRep((char)0, Character.MAX_VALUE, 267);
        op[267] = getOpcodeRnm(70, 325); // xdi-char
        {char[] a = {125}; op[268] = getOpcodeTls(a);}
        {int[] a = {270,271,273}; op[269] = getOpcodeCat(a);}
        {char[] a = {58,105,112,118,54,58}; op[270] = getOpcodeTls(a);}
        op[271] = getOpcodeRep((char)4, (char)4, 272);
        op[272] = getOpcodeRnm(73, 337); // HEXDIG
        op[273] = getOpcodeRep((char)7, Character.MAX_VALUE, 274);
        {int[] a = {275,276}; op[274] = getOpcodeCat(a);}
        {char[] a = {58}; op[275] = getOpcodeTls(a);}
        op[276] = getOpcodeRep((char)4, (char)4, 277);
        op[277] = getOpcodeRnm(73, 337); // HEXDIG
        {int[] a = {279,280,281,282,283,284,285,286,287,288,289}; op[278] = getOpcodeCat(a);}
        {char[] a = {58,117,117,105,100,58}; op[279] = getOpcodeTls(a);}
        op[280] = getOpcodeRnm(60, 290); // time-low
        {char[] a = {45}; op[281] = getOpcodeTls(a);}
        op[282] = getOpcodeRnm(61, 292); // time-mid
        {char[] a = {45}; op[283] = getOpcodeTls(a);}
        op[284] = getOpcodeRnm(62, 294); // time-high
        {char[] a = {45}; op[285] = getOpcodeTls(a);}
        op[286] = getOpcodeRnm(63, 296); // clock-seq
        op[287] = getOpcodeRnm(64, 298); // clock-seq-low
        {char[] a = {45}; op[288] = getOpcodeTls(a);}
        op[289] = getOpcodeRnm(65, 300); // node
        op[290] = getOpcodeRep((char)8, (char)8, 291);
        op[291] = getOpcodeRnm(73, 337); // HEXDIG
        op[292] = getOpcodeRep((char)4, (char)4, 293);
        op[293] = getOpcodeRnm(73, 337); // HEXDIG
        op[294] = getOpcodeRep((char)4, (char)4, 295);
        op[295] = getOpcodeRnm(73, 337); // HEXDIG
        op[296] = getOpcodeRep((char)2, (char)2, 297);
        op[297] = getOpcodeRnm(73, 337); // HEXDIG
        op[298] = getOpcodeRep((char)2, (char)2, 299);
        op[299] = getOpcodeRnm(73, 337); // HEXDIG
        op[300] = getOpcodeRep((char)12, (char)12, 301);
        op[301] = getOpcodeRnm(73, 337); // HEXDIG
        op[302] = getOpcodeRep((char)1, Character.MAX_VALUE, 303);
        {int[] a = {304,305}; op[303] = getOpcodeAlt(a);}
        op[304] = getOpcodeRnm(70, 325); // xdi-char
        {char[] a = {58}; op[305] = getOpcodeTls(a);}
        {int[] a = {307,308,309}; op[306] = getOpcodeAlt(a);}
        op[307] = getOpcodeRnm(70, 325); // xdi-char
        op[308] = getOpcodeRnm(69, 318); // context-symbol
        op[309] = getOpcodeRnm(68, 310); // nonparen-delim
        {int[] a = {311,312,313,314,315,316,317}; op[310] = getOpcodeAlt(a);}
        {char[] a = {47}; op[311] = getOpcodeTls(a);}
        {char[] a = {63}; op[312] = getOpcodeTls(a);}
        {char[] a = {35}; op[313] = getOpcodeTls(a);}
        {char[] a = {91}; op[314] = getOpcodeTls(a);}
        {char[] a = {93}; op[315] = getOpcodeTls(a);}
        {char[] a = {39}; op[316] = getOpcodeTls(a);}
        {char[] a = {44}; op[317] = getOpcodeTls(a);}
        {int[] a = {319,320,321,322,323,324}; op[318] = getOpcodeAlt(a);}
        {char[] a = {33}; op[319] = getOpcodeTls(a);}
        {char[] a = {42}; op[320] = getOpcodeTls(a);}
        {char[] a = {61}; op[321] = getOpcodeTls(a);}
        {char[] a = {64}; op[322] = getOpcodeTls(a);}
        {char[] a = {43}; op[323] = getOpcodeTls(a);}
        {char[] a = {36}; op[324] = getOpcodeTls(a);}
        {int[] a = {326,327,328,329,330,331,332}; op[325] = getOpcodeAlt(a);}
        op[326] = getOpcodeRnm(71, 333); // ALPHA
        op[327] = getOpcodeRnm(72, 336); // DIGIT
        {char[] a = {45}; op[328] = getOpcodeTls(a);}
        {char[] a = {46}; op[329] = getOpcodeTls(a);}
        {char[] a = {95}; op[330] = getOpcodeTls(a);}
        {char[] a = {126}; op[331] = getOpcodeTls(a);}
        op[332] = getOpcodeTrg((char)128, (char)65533);
        {int[] a = {334,335}; op[333] = getOpcodeAlt(a);}
        op[334] = getOpcodeTrg((char)65, (char)90);
        op[335] = getOpcodeTrg((char)97, (char)122);
        op[336] = getOpcodeTrg((char)48, (char)57);
        {int[] a = {338,339}; op[337] = getOpcodeAlt(a);}
        op[338] = getOpcodeTrg((char)48, (char)57);
        op[339] = getOpcodeTrg((char)65, (char)70);
        {int[] a = {341,342,343}; op[340] = getOpcodeAlt(a);}
        {char[] a = {13}; op[341] = getOpcodeTbs(a);}
        {char[] a = {10}; op[342] = getOpcodeTbs(a);}
        {int[] a = {344,345}; op[343] = getOpcodeCat(a);}
        {char[] a = {13}; op[344] = getOpcodeTbs(a);}
        {char[] a = {10}; op[345] = getOpcodeTbs(a);}
        {char[] a = {34}; op[346] = getOpcodeTbs(a);}
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
        out.println("absolute                =          local-root \"/()/\" context");
        out.println("peer-relative           =           peer-root \"/()/\" context");
        out.println("context-relative        =             context \"/()/\" relative-context");
        out.println("class-relative          =       class-context \"/()/\" instance-context");
        out.println("absolute-inverse        =          context \"/$is()/\" local-root");
        out.println("peer-inverse            =          context \"/$is()/\" peer-root");
        out.println("context-inverse         = relative-context \"/$is()/\" context");
        out.println("class-inverse           = instance-context \"/$is()/\" class-context");
        out.println("");
        out.println("relational-statement    = inverse-relational / direct-relational / inner-relational");
        out.println("direct-relational       = context \"/\"    context \"/\" context");
        out.println("inverse-relational      = context \"/$is\" context \"/\" context");
        out.println("inner-relational        = context \"/\"    context \"/\" inner-statement");
        out.println("");
        out.println("inner-statement         = \"(\" xdi-statement \")\"");
        out.println("");
        out.println("literal-statement       = literal-context value-context \":/:/\" json-value");
        out.println("");
        out.println("context                 = ( peer-root [ relative-context ] ) / relative-context");
        out.println("relative-context        = 1*subpath");
        out.println("");
        out.println("class-context           = [ peer-root ] class-path");
        out.println("class-path              = ( subpath class-path ) / class");
        out.println("");
        out.println("instance-context        = ( instance / element ) [ relative-context ]");
        out.println("");
        out.println("literal-context         = [ peer-root ] literal-path");
        out.println("literal-path            = ( subpath literal-path ) / attribute-singleton / attribute-pair");
        out.println("attribute-pair          = attribute-class ( instance / element )");
        out.println("");
        out.println("value-context           = \"<<\" 1*type-class \">>\"");
        out.println("");
        out.println("root                    = local-root / peer-root / inner-root");
        out.println("local-root              = \"()\"");
        out.println("peer-root               = \"(\" ( context / iri-chars ) \")\"");
        out.println("inner-root              = \"(\" context \"/\" context \")\"");
        out.println("");
        out.println("xref                    = \"(\" ( context / iri-chars / xdi-chars ) \")\"");
        out.println("");
        out.println("subpath                 = singleton / ( class [ instance / element ] )");
        out.println("subsegment              = singleton / class / instance / element / root / inner-statement");
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
