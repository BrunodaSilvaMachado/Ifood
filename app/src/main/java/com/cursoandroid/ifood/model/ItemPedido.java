package com.cursoandroid.ifood.model;

public class ItemPedido {
    private String idProduto;
    private String nome;
    private int quandtidade;
    private Double preco;

    public ItemPedido(String idProduto, String nome, int quandtidade, Double preco) {
        this.idProduto = idProduto;
        this.nome = nome;
        this.quandtidade = quandtidade;
        this.preco = preco;
    }

    public ItemPedido() {
    }

    public String getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(String idProduto) {
        this.idProduto = idProduto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getQuandtidade() {
        return quandtidade;
    }

    public void setQuandtidade(int quandtidade) {
        this.quandtidade = quandtidade;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null){
            return false;
        }else if (this == obj){
            return true;
        }else return this.getIdProduto().equals(((ItemPedido) obj).getIdProduto());
    }
}
