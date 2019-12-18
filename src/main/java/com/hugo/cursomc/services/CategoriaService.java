package com.hugo.cursomc.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hugo.cursomc.domain.Categoria;
import com.hugo.cursomc.repositories.CategoriaRepository;
import com.hugo.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class CategoriaService {
	
	@Autowired
	private CategoriaRepository repo;
	
	//aqui muda no curso para procurar por id
	public Categoria find(Integer id) {
		Optional<Categoria> obj = repo.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
		"Objeto não encontrado! Id: " + id + ", Tipo: " + Categoria.class.getName()));
		}
	
	//Salvar
	public Categoria insert(Categoria obj) {
		//Se o obj for nullo insere um nod obj com id
		obj.setId(null);
		return repo.save(obj);
	}
	
	//Atualizar
	public Categoria update(Categoria obj) {
		//Verifica se o id existe, se nao existe ele lança uma exception do metodo find() acima
		find(obj.getId());
		return repo.save(obj);
	}

}

