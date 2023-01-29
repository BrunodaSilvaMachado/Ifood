package com.cursoandroid.ifood.model;

import com.cursoandroid.ifood.service.DatabaseService;

import java.util.List;

public class Pedido implements DatabaseService.ModelId {
    public static final String STATUS_PENDENTE = "PENDENTE";
    public static final String STATUS_CONFIRMADO = "CONFIRMADO";
    public static final String STATUS_FINALIZADO = "FINALIZADO";
    private String id;
    private String idUsuario;
    private String idCompany;
    private String username;
    private String tel;
    private Address address;
    private List<ItemPedido> itemPedidos;
    private Double total;
    private String status;
    private int metodoPagamento;
    private String observacao;

    public Pedido() {
        this.status = STATUS_PENDENTE;
    }

    public Pedido(String idUsuario, String idCompany) {
        this.idUsuario = idUsuario;
        this.idCompany = idCompany;
        this.status = STATUS_PENDENTE;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdCompany() {
        return idCompany;
    }

    public void setIdCompany(String idCompany) {
        this.idCompany = idCompany;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<ItemPedido> getItemPedidos() {
        return itemPedidos;
    }

    public void setItemPedidos(List<ItemPedido> itemPedidos) {
        this.itemPedidos = itemPedidos;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(int metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }
}
