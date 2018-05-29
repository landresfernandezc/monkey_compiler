package interpreter;

import generated.MonkeyParser;
import generated.MonkeyParserBaseVisitor;
import interfaz.Interfaz;

import java.util.LinkedList;

public class Interpreter extends MonkeyParserBaseVisitor {
    DataStorage almacen =new DataStorage();
    EvaluationStack pila=new EvaluationStack();
    int indicePila=0;
    int indiceAlmacen=0;

    int tipo_Boolean_true=1;
    int tipo_Boolean_false=111;
    int tipo_Entero=2;
    int tipo_String=3;
    int tipo_Identifier=4;
    int tipo_ArrayLiteral=5;
    int tipo_ArrayFunctions=6;
    int tipo_NULL=13;
    int tipoError=-1;
    int tipo_HashLiteral=12;
    int tipoFnLEN=7;
    int tipoFnFIRST=8;
    int tipoFnLAST=9;
    int tipoFnREST=10;
    int tipoFnPUSH=11;
    int tipoNeutro=0;

    @Override
    public Object visitProgram_monkey(MonkeyParser.Program_monkeyContext ctx){
        for(MonkeyParser.StatementContext ele:ctx.statement())
            visit(ele);
        return null;
    }

    @Override
    public Object visitStatement_let_monkey(MonkeyParser.Statement_let_monkeyContext ctx) {
        visit(ctx.letStatement());
        return null;
    }

    @Override
    public Object visitStatement_return_monkey(MonkeyParser.Statement_return_monkeyContext ctx) {
        visit(ctx.returnStatement());
        return null;
    }

    @Override
    public Object visitStatement_expressionStatement_monkey(MonkeyParser.Statement_expressionStatement_monkeyContext ctx) {
        visit(ctx.expressionStatement());
        return null;

    }

    public boolean existe(String name){
        for(int x=0;x<this.almacen.getData().size();x++){
            if(this.almacen.getData().get(x).getName().equals(name)){
                return true;
            }
        }
        return false;
    }
    @Override
    public Object visitLetStatement_monkey(MonkeyParser.LetStatement_monkeyContext ctx) {
        visit(ctx.expression());
        //sacar de la pila
        if(this.pila.size()>0){
            ElementoStack el = this.pila.popValue();
            boolean existe=existe(((MonkeyParser.IdASTContext)ctx.identifier()).ID().getText());
            if(existe==true){
                this.almacen.modifyData(((MonkeyParser.IdASTContext)ctx.identifier()).ID().getText(),el.getValor(),el.getTipo());
                this.almacen.printDataStorage();
            }else{
                this.almacen.addData(((MonkeyParser.IdASTContext)ctx.identifier()).ID().getText(),el.getValor(),el.getTipo(),this.almacen.getActualStorageIndex());
                this.almacen.printDataStorage();
            }
        }
        return null;
    }
    @Override
    public Object visitReturnStatement_monkey(MonkeyParser.ReturnStatement_monkeyContext ctx) {
        visit(ctx.expression());
        return null;
    }

    @Override
    public Object visitExpressionStatement_monkey(MonkeyParser.ExpressionStatement_monkeyContext ctx) {
        visit(ctx.expression());
        return null;
    }

    @Override
    public Object visitExpression_monkey(MonkeyParser.Expression_monkeyContext ctx) {
        visit(ctx.additionExpression());
        visit(ctx.comparison());
        return null;
    }

    @Override
    public Object visitComparisonLess_monkey(MonkeyParser.ComparisonLess_monkeyContext ctx) {
        for(MonkeyParser.AdditionExpressionContext ele:ctx.additionExpression())
            visit(ele);
        return null;
    }

    @Override
    public Object visitComparisonPlus_monkey(MonkeyParser.ComparisonPlus_monkeyContext ctx) {
        for(MonkeyParser.AdditionExpressionContext ele:ctx.additionExpression())
            visit(ele);
        return null;
    }

    @Override
    public Object visitComparisonLessEqual_monkey(MonkeyParser.ComparisonLessEqual_monkeyContext ctx) {
        for(MonkeyParser.AdditionExpressionContext ele:ctx.additionExpression())
            visit(ele);
        return null;
    }

    @Override
    public Object visitComparisonPlusEqual_monkey(MonkeyParser.ComparisonPlusEqual_monkeyContext ctx) {
        for(MonkeyParser.AdditionExpressionContext ele:ctx.additionExpression())
            visit(ele);
        return null;
    }

    @Override
    public Object visitComparisonEqualEqual_monkey(MonkeyParser.ComparisonEqualEqual_monkeyContext ctx) {
        for(MonkeyParser.AdditionExpressionContext ele:ctx.additionExpression())
            visit(ele);
        return null;
    }

    @Override
    public Object visitAdittionExpression_monkey(MonkeyParser.AdittionExpression_monkeyContext ctx) {
        visit(ctx.multiplicationExpression());
        visit(ctx.additionFactor());
        return null;
    }
    private Integer OperarNumeros(Integer v1, Integer v2,String operacion){
        Integer res=new Integer(0);
        if (operacion=="+"){
            res = v1 + v2;
        }
        else if (operacion=="-"){
            res = v2 - v1;
        }
        else if(operacion=="*"){
            res=v1*v2;
        }
        else if(operacion=="/"){
            res=v2/v1;
        }
        return res;
    }
    private boolean existanComillas(String cadena){
        for(int x=0;x<cadena.length();x++){
            if(cadena.charAt(x)=='"'){
                return true;
            }

        }
        return false;
    }
    private String Concatenar(String v1, String v2){
        String res=new String();
        res=v2+v1;
        while (existanComillas(res)){
            for(int x=0;x<res.length();x++){
                if(res.charAt(x)=='"'){
                    res=res.substring(0,x)+res.substring(x+1,res.length());
                }
            }
        }
        return res;
    }
    @Override
    public Object visitAdittionFactorSUMA_monkey(MonkeyParser.AdittionFactorSUMA_monkeyContext ctx) {
        int largo=ctx.multiplicationExpression().size();
        for(int i=0; i<largo;i++){
            //validar si es suma o resta dependiendo del size de la lista de token suma o resta
                visit(ctx.multiplicationExpression(i));
                ElementoStack elemento1=this.pila.popValue();
                ElementoStack elemento2=this.pila.popValue();
                if(elemento1.getTipo()==tipo_String && elemento2.getTipo()==tipo_String){
                        String resultado=Concatenar((String) elemento1.getValor(),(String)elemento2.getValor());
                        Object resulto=(Object)resultado;
                        System.out.println("Resultado="+resultado);
                        this.pila.pushValue(new ElementoStack(resulto,tipo_String));
                }
                else if((elemento1.getTipo()!=tipo_String && elemento2.getTipo()==tipo_String)|(elemento1.getTipo()==tipo_String && elemento2.getTipo()!=tipo_String)){
                        Interfaz.msjsError.add("Error de tipos en la operacion, no se pueden sumar enteros con strings");
                }
                else{
                        int resultado=OperarNumeros((Integer)elemento1.getValor(),(Integer)elemento2.getValor(),"+");
                        Object resulto=(Object)resultado;
                        System.out.println("Resultado="+resultado);
                        this.pila.pushValue(new ElementoStack(resulto,tipo_Entero));
                    }
        }
        return null;
    }
    @Override
    public Object visitAdittionFactorRESTA_monkey(MonkeyParser.AdittionFactorRESTA_monkeyContext ctx) {
        int largo=ctx.multiplicationExpression().size();
        for(int i=0; i<largo;i++){
            //validar si es suma o resta dependiendo del size de la lista de token suma o resta
            visit(ctx.multiplicationExpression(i));
            ElementoStack elemento1=this.pila.popValue();
            ElementoStack elemento2=this.pila.popValue();
            if((elemento1.getTipo()!=tipo_String && elemento2.getTipo()==tipo_String)|(elemento1.getTipo()==tipo_String && elemento2.getTipo()!=tipo_String)){
                    Interfaz.msjsError.add("Error de tipos en la operacion, no se pueden sumar enteros con strings");
                }
            else{
                    int resultado=OperarNumeros((Integer)elemento1.getValor(),(Integer)elemento2.getValor(),"-");
                    Object resulto=(Object)resultado;
                    System.out.println("Resultado="+resultado);
                    this.pila.pushValue(new ElementoStack(resulto,tipo_Entero));
                }

        }
        return null;
    }
    @Override
    public Object visitMultiplicationExpression_monkey(MonkeyParser.MultiplicationExpression_monkeyContext ctx) {
        visit(ctx.elementExpression());
        visit(ctx.multiplicationFactor());
        return null;
    }
    @Override
    public Object visitMultiplicationFactorMUL_monkey(MonkeyParser.MultiplicationFactorMUL_monkeyContext ctx) {
        int largo=ctx.elementExpression().size();
        for(int i=0; i<largo;i++){
            //validar si es suma o resta dependiendo del size de la lista de token suma o resta
            visit(ctx.elementExpression(i));
            ElementoStack elemento1=this.pila.popValue();
            ElementoStack elemento2=this.pila.popValue();
                if((elemento1.getTipo()!=tipo_String && elemento2.getTipo()==tipo_String)|(elemento1.getTipo()==tipo_String && elemento2.getTipo()!=tipo_String)){
                    Interfaz.msjsError.add("Error de tipos en la operacion, no se pueden sumar enteros con strings");
                }
                else{
                    int resultado=OperarNumeros((Integer)elemento1.getValor(),(Integer)elemento2.getValor(),"*");
                    Object resulto=(Object)resultado;
                    System.out.println("Resultado="+resultado);
                    this.pila.pushValue(new ElementoStack(resulto,tipo_Entero));
                }
        }
        return null;
    }
    @Override
    public Object visitMultiplicationFactorDIV_monkey(MonkeyParser.MultiplicationFactorDIV_monkeyContext ctx) {
        int largo=ctx.elementExpression().size();
        for(int i=0; i<largo;i++){
            //validar si es suma o resta dependiendo del size de la lista de token suma o resta
            visit(ctx.elementExpression(i));
            ElementoStack elemento1=this.pila.popValue();
            ElementoStack elemento2=this.pila.popValue();
            if((elemento1.getTipo()!=tipo_String && elemento2.getTipo()==tipo_String)|(elemento1.getTipo()==tipo_String && elemento2.getTipo()!=tipo_String)){
                    Interfaz.msjsError.add("Error de tipos en la operacion, no se pueden sumar enteros con strings");
                }
            else{
                    int resultado=OperarNumeros((Integer)elemento1.getValor(),(Integer)elemento2.getValor(),"/");
                    Object resulto=(Object)resultado;
                    this.pila.pushValue(new ElementoStack(resulto,tipo_Entero));
                }
        }
        return null;
    }
    public ElementoStack devuelveElementoLista(String variable,int position){
        for(int x=0;x<this.almacen.getData().size();x++){
            if(this.almacen.getData(x).getName().equals(variable)){
                ElementoDataStorage element=this.almacen.getData(x);
                LinkedList<ElementoStack> lista= (LinkedList<ElementoStack>) element.getValue();
                for(int y=0;y<lista.size();y++){
                    if(y==position){
                        return lista.get(x);
                    }
                }
            }
        }
        return null;
    }
    @Override
    public Object visitElementExprssionPEElementAccess_monkey(MonkeyParser.ElementExprssionPEElementAccess_monkeyContext ctx) {
        visit(ctx.primitiveExpression());
        if(existe(ctx.primitiveExpression().getText())){
            System.out.println("No mames");
            String elemento= (String) visit(ctx.elementAccess());
            System.out.println("Prueba:"+this.almacen.devuelve(ctx.primitiveExpression().getText()).getTipo());
            if(this.almacen.devuelve(ctx.primitiveExpression().getText()).getTipo()==tipo_ArrayLiteral){
                System.out.println("Entro a la prueba");
                String variable=ctx.primitiveExpression().getText();
                ElementoStack elementoPila=this.pila.popValue();
                Integer indiceLista= (Integer) elementoPila.getValor();
                this.pila.popValue();
                this.pila.pushValue(new ElementoStack(devuelveElementoLista(variable,indiceLista),elementoPila.getTipo()));
            }
        }
        else{
            Interfaz.msjsError.add("No existe la variable "+ctx.primitiveExpression().getText());
        }
        return null;
    }

    @Override
    public Object visitElementExprssionPECallExpression_monkey(MonkeyParser.ElementExprssionPECallExpression_monkeyContext ctx) {
        visit(ctx.primitiveExpression());
        visit(ctx.callExpression());
        return null;
    }

    @Override
    public Object visitElementExpressionPE_monkey(MonkeyParser.ElementExpressionPE_monkeyContext ctx) {
        visit(ctx.primitiveExpression());
        return null;
    }

    @Override
    public Object visitElementAcces_monkey(MonkeyParser.ElementAcces_monkeyContext ctx) {
        visit(ctx.expression());
        return ctx.expression().getText();
    }

    @Override
    public Object visitCallExpression_monkey(MonkeyParser.CallExpression_monkeyContext ctx) {
        visit(ctx.expressionList());
        return null;
    }

    @Override
    public Object visitPEInteger_monkey(MonkeyParser.PEInteger_monkeyContext ctx) {
        this.pila.pushValue(new ElementoStack(Integer.parseInt(ctx.getText()),tipo_Entero));
        return null;
    }

    @Override
    public Object visitPEString_monkey(MonkeyParser.PEString_monkeyContext ctx){
        this.pila.pushValue(new ElementoStack(ctx.getText(),tipo_String));
        return null;
    }

    @Override
    public Object visitPEIdentifier_monkey(MonkeyParser.PEIdentifier_monkeyContext ctx) {
        visit(ctx.identifier());
        return null;
    }

    @Override
    public Object visitPETrue_monkey(MonkeyParser.PETrue_monkeyContext ctx) {
        this.pila.pushValue(new ElementoStack(ctx.getText(),tipo_Boolean_true));
        return null;
    }

    @Override
    public Object visitPEFalse_monkey(MonkeyParser.PEFalse_monkeyContext ctx) {
        this.pila.pushValue(new ElementoStack(ctx.getText(),tipo_Boolean_false));
        return null;
    }
    @Override
    public Object visitPEExpression_monkey(MonkeyParser.PEExpression_monkeyContext ctx) {
        visit(ctx.expression());
        return null;
    }

    @Override
    public Object visitPEArrayLiteral_monkey(MonkeyParser.PEArrayLiteral_monkeyContext ctx) {
        this.pila.pushValue(new ElementoStack(ctx.getText(),tipo_ArrayLiteral));
        visit(ctx.arrayLiteral());
        return null;
    }
    @Override
    public Object visitPEArrayFunctions_monkey(MonkeyParser.PEArrayFunctions_monkeyContext ctx) {
        visit(ctx.arrayFunctions());
        visit(ctx.expressionList());
        this.pila.pushValue(new ElementoStack(ctx.getText(),tipo_ArrayFunctions));
        return null;
    }
    @Override
    public Object visitPEFunctionsLiteral_monkey(MonkeyParser.PEFunctionsLiteral_monkeyContext ctx) {
        visit(ctx.functionLiteral());
        this.pila.pushValue(new ElementoStack(ctx.getText(),0));
        return null;
    }
    @Override
    public Object visitPEHashLiteral_monkey(MonkeyParser.PEHashLiteral_monkeyContext ctx) {
        visit(ctx.hashLiteral());
        this.pila.pushValue(new ElementoStack(ctx.getText(),tipo_HashLiteral));
        return null;
    }

    @Override
    public Object visitPEPrintExpression_monkey(MonkeyParser.PEPrintExpression_monkeyContext ctx) {
        visit(ctx.printExpression());
        return null;
    }
    @Override
    public Object visitPEIfExpression_monkey(MonkeyParser.PEIfExpression_monkeyContext ctx) {
        visit(ctx.ifExpression());
        return null;
    }
    @Override
    public Object visitArrayFunctionsLEN_monkey(MonkeyParser.ArrayFunctionsLEN_monkeyContext ctx) {
        return null;
    }

    @Override
    public Object visitArrayFunctionsFIRST_monkey(MonkeyParser.ArrayFunctionsFIRST_monkeyContext ctx) {
        return null;
    }

    @Override
    public Object visitArrayFunctionsLAST_monkey(MonkeyParser.ArrayFunctionsLAST_monkeyContext ctx) {
        return null;
    }

    @Override
    public Object visitArrayFunctionsREST_monkey(MonkeyParser.ArrayFunctionsREST_monkeyContext ctx) {
        return null;
    }

    @Override
    public Object visitArrayFunctionsPUSH_monkey(MonkeyParser.ArrayFunctionsPUSH_monkeyContext ctx) {
        return null;
    }

    @Override
    public Object visitArrayLiteral_monkey(MonkeyParser.ArrayLiteral_monkeyContext ctx) {
        visit(ctx.expressionList());
        return null;
    }
    @Override
    public Object visitFunctionLiteral_monkey(MonkeyParser.FunctionLiteral_monkeyContext ctx) {
        visit(ctx.functionParameters());
        visit(ctx.blockStatement());
        return null;
    }

    @Override
    public Object visitFunctionParameters_monkey(MonkeyParser.FunctionParameters_monkeyContext ctx) {
        visit(ctx.moreIdentifiers());
        return null;
    }
    @Override
    public Object visitMoreIdentifiers_monkey(MonkeyParser.MoreIdentifiers_monkeyContext ctx) {
        return null;
    }

    @Override
    public Object visitHashLiteral_monkey(MonkeyParser.HashLiteral_monkeyContext ctx) {
        visit(ctx.hashContent());
        visit(ctx.moreHashContent());
        return null;
    }

    @Override
    public Object visitHashContet_monkey(MonkeyParser.HashContet_monkeyContext ctx) {
        visit(ctx.expression(0));
        visit(ctx.expression(1));
        return null;
    }

    @Override
    public Object visitMoreHashContet_monkey(MonkeyParser.MoreHashContet_monkeyContext ctx) {
        if(ctx.hashContent().size()==0){
            visit(ctx.hashContent(0));
            ElementoStack valor=this.pila.popValue();
            ElementoStack clave=this.pila.popValue();
            if(clave.getTipo()==tipo_String || clave.getTipo()==tipo_Entero){
                Data data=new Data(clave,valor);
                LinkedList<Data> lista=new LinkedList<>();
                lista.add(data);
                JSON json=new JSON(lista);
                ElementoStack elemento=new ElementoStack(json,tipo_HashLiteral);
                this.pila.pushValue(elemento);
            }
            else{
                Interfaz.msjsError.add("La clave del hashLiteral solo puede ser entero o string");
            }
        }
        else{
            LinkedList<Data> lista=new LinkedList<>();
            for(int x=0;x<ctx.hashContent().size();x++){
                visit(ctx.hashContent(x));
                ElementoStack valor=this.pila.popValue();
                ElementoStack clave=this.pila.popValue();
                if(clave.getTipo()==tipo_String || clave.getTipo()==tipo_Entero){
                    Data data=new Data(clave,valor);
                    lista.add(data);
                }
                else{
                    Interfaz.msjsError.add("La clave del hashLiteral solo puede ser entero o string");
                }
            }
            JSON json=new JSON(lista);
            ElementoStack elemento=new ElementoStack(json,tipo_HashLiteral);
            this.pila.pushValue(elemento);
        }
        return null;
    }

    @Override
    public Object visitExpressionListExpression_monkey(MonkeyParser.ExpressionListExpression_monkeyContext ctx) {
        visit(ctx.expression());
        visit(ctx.moreExpressions());
        return null;
    }

    @Override
    public Object visitExpressionListVacio_monkey(MonkeyParser.ExpressionListVacio_monkeyContext ctx) {
        return null;
    }

    @Override
    public Object visitMoreExpression_monkey(MonkeyParser.MoreExpression_monkeyContext ctx) {
        LinkedList<Object> listaElementos=new LinkedList<>();
        if(ctx.expression().size()==1){
            visit(ctx.expression(0));
            listaElementos.add(this.pila.popValue());
            this.pila.pushValue(new ElementoStack(listaElementos,tipo_ArrayLiteral));
        }
        else if(ctx.expression().size()>0){
            visit(ctx.expression(0));
            listaElementos.add(this.pila.popValue());
            for(int i=1; i<ctx.expression().size();i++) {
                visit(ctx.expression(i));
                listaElementos.add(this.pila.popValue());
            }
            this.pila.pushValue(new ElementoStack(listaElementos,tipo_ArrayLiteral));
        }
        return null;
    }

    @Override
    public Object visitPrintExpression_monkey(MonkeyParser.PrintExpression_monkeyContext ctx) {
        visit(ctx.expression());
        return ctx.expression();
    }

    @Override
    public Object visitIfExpression_monkey(MonkeyParser.IfExpression_monkeyContext ctx) {
        visit(ctx.expression());
        visit(ctx.blockStatement(0));
        visit(ctx.blockStatement(1));
        return ctx.expression();
    }

    @Override
    public Object visitBlockStatement_monkey(MonkeyParser.BlockStatement_monkeyContext ctx) {
        for(MonkeyParser.StatementContext ele:ctx.statement()){
            visit(ele);
        }
        return null;
    }

    @Override
    public Object visitIdAST(MonkeyParser.IdASTContext ctx){
        String identifierIndex= ctx.ID().getText();
        ElementoDataStorage elemento=this.almacen.getData(identifierIndex);
        if(elemento.getName().equals("null") && elemento.getValue().equals(-1)){
            Interfaz.msjsError.add("Error, la variable "+identifierIndex+ " no existe");
        }
        else{
            ElementoDataStorage element = this.almacen.getData(identifierIndex);
            this.pila.pushValue(new ElementoStack(element.getValue(),element.getTipo()));
        }
        return null;
    }
}
