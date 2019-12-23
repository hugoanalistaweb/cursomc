package com.hugo.cursomc.services;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.hugo.cursomc.domain.Cidade;
import com.hugo.cursomc.domain.Cliente;
import com.hugo.cursomc.domain.Endereco;
import com.hugo.cursomc.domain.enums.TipoCliente;
import com.hugo.cursomc.dto.ClienteDTO;
import com.hugo.cursomc.dto.ClienteNewDTO;
import com.hugo.cursomc.repositories.ClienteRepository;
import com.hugo.cursomc.repositories.EnderecoRepository;
import com.hugo.cursomc.services.exceptions.DataIntegrityException;
import com.hugo.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {
	
	@Autowired
	private ClienteRepository repo;
	
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	//aqui muda no curso para procurar por id
	public Cliente find(Integer id) {
		Optional<Cliente> obj = repo.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
		"Objeto não encontrado! Id: " + id + ", Tipo: " + Cliente.class.getName()));
		}
	
		//Salvar
		@Transactional
		public Cliente insert(Cliente obj) {
			//Se o obj for nullo insere um nod obj com id
			obj.setId(null);
			obj = repo.save(obj);
			enderecoRepository.saveAll(obj.getEnderecos());
			return obj;
		}
	
		//Atualizar
		public Cliente update(Cliente obj) {
			//Verifica se o id existe, se nao existe ele lança uma exception do metodo find() acima
			Cliente newObj = find(obj.getId());
			updateData(newObj, obj);
			return repo.save(newObj);
		}
		
		//Deletar
		public void delete(Integer id) {
			//Verifica se o id existe, se nao existe ele lança uma exception do metodo find() acima
			find(id);
			
			try {
					
			repo.deleteById(id);
			
			}catch (DataIntegrityViolationException  e) {
				
				throw new DataIntegrityException("Não é possível excluir porque há entidades relacionadas!");
			}
		}
		
		public List<Cliente> findAll(){
			return repo.findAll();
		}
		
		public Page<Cliente> findPage(Integer page, Integer linesPerPage, String orderBy, String direction){
			PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction),
							orderBy);
			return repo.findAll(pageRequest);
					
		}
		public Cliente fromDTO(ClienteDTO objDto) {
			
			return new Cliente(objDto.getId(),objDto.getNome(),objDto.getEmail(),null,null);
		}
		
		public Cliente fromDTO(ClienteNewDTO objDto) {
			Cliente cli = new Cliente(null, objDto.getNome(), objDto.getEmail(), objDto.getCpfOuCnpj(), TipoCliente.toEnum(objDto.getTipo()));
			Cidade cid = new Cidade(objDto.getCidadeId(), null, null);
			Endereco end = new Endereco(null, objDto.getLogradouro(), objDto.getNumero(), objDto.getComplemento(), objDto.getBairro(), objDto.getCep(), cli, cid);
			cli.getEnderecos().add(end);
			cli.getTelefones().add(objDto.getTelefone1());
			
			if (objDto.getTelefone2()!=null) {
				cli.getTelefones().add(objDto.getTelefone2());
			}
			
			if (objDto.getTelefone3()!=null) {
				cli.getTelefones().add(objDto.getTelefone3());
			}
			
			return cli;
		}
		
		private void updateData (Cliente newObj, Cliente obj) {
			newObj.setNome(obj.getNome());
			newObj.setEmail(obj.getEmail());
		}
		
	}


